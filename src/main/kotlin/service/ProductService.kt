package com.example.service

import com.example.model.Products
import com.example.model.dto.ProductDTO
import kotlinx.serialization.Serializable
import com.example.cache.RedisClientProvider
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update
import java.util.UUID

class ProductService {
    private val ttlSeconds = 600L
    fun insertProduct(name: String, description: String, price: Int) {
        val creationDate = LocalDateTime.now();
        transaction {
            val productId = Products.insert {
                it[Products.name] = name
                it[Products.description] = description
                it[Products.price] = price
                it[Products.createdAt] = creationDate
            } get Products.id

            ProductResponse(
                id = productId.toString(),
                name = name,
                description = description,
                price = price,
                createdAt = "now"
            )
        }
    }

    fun getAllProducts(nameFilter: String? = null): List<ProductResponse> {
        return transaction {
            val query = if (!nameFilter.isNullOrBlank()) {
                Products.select { Products.name like "%$nameFilter%" }
            } else {
                Products.selectAll()
            }

            query.map {
                ProductResponse(
                    id = it[Products.id].toString(),
                    name = it[Products.name],
                    description = it[Products.description],
                    price = it[Products.price],
                    createdAt = it[Products.createdAt].toString()
                )
            }
        }
    }

    fun updateProduct(product: ProductDTO) {
        transaction {
            Products.update({ Products.id eq product.id }) {
                it[Products.name] = product.name;
                it[Products.description] = product.description;
                it[Products.price] = product.price;
            }
        }
        val redis = RedisClientProvider.commands()
        redis.del("product:${product.id}")
    }

    fun getProductById(id: String): ProductResponse? {
        val redis = RedisClientProvider.commands()
        val cacheKey = "product:$id"

        val cached = redis.get(cacheKey)
        if (cached != null) {
            val parts = cached.split("|")
            return ProductResponse(
                id = parts[0],
                name = parts[1],
                description = parts[2],
                price = parts[3].toInt(),
                createdAt = parts[4]
            )
        }

        val product = transaction {
            Products.selectAll().map {
                ProductResponse(
                    id = it[Products.id].toString(),
                    name = it[Products.name],
                    description = it[Products.description],
                    price = it[Products.price],
                    createdAt = it[Products.createdAt].toString()
                )
            }.find { it.id == id }
        }

        if (product != null) {
            val value = "${product.id}|${product.name}|${product.description}|${product.price}|${product.createdAt}"
            redis.setex(cacheKey, ttlSeconds, value)
        }

        return product
    }

    fun deleteProduct(product: ProductDTO) {
             transaction {
            Products.deleteWhere { Products.id eq product.id } > 0
        }
        val redis = RedisClientProvider.commands()
        redis.del("product:${product.id}")
    }

    fun get(productId: UUID) : ProductDTO? {
        return transaction {
            Products.select { Products.id eq productId }
                .mapNotNull {
                    ProductDTO(
                        id = it[Products.id],
                        name = it[Products.name],
                        description = it[Products.description],
                        price = it[Products.price],
                        stock = it[Products.stock],
                        creationDate = it[Products.createdAt]
                    )
                }
                .singleOrNull()
        }
    }

    fun findProductById(productId: UUID) : ProductResponse? {
        return transaction {
            Products.select { Products.id eq productId }
                .mapNotNull {
                    ProductResponse(
                        id = it[Products.id].toString(),
                        name = it[Products.name],
                        description = it[Products.description],
                        price = it[Products.price],
                        createdAt = it[Products.createdAt].toString()
                    )
                }
                .singleOrNull()
        }
    }

    fun decreaseStock(productId: UUID, count: Int) {
        var product: ProductDTO = get(productId)!!;
        product = ProductDTO(product.id, product.name, product.description, product.price, product.stock - count, product.creationDate)
        if (product.stock <= 0) throw IllegalArgumentException("Negative stock")
        updateProduct(product)
    }
}

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val createdAt: String
)