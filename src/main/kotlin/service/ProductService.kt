package com.example.service

import com.example.model.Products
import com.example.model.dto.ProductDTO
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update

class ProductService {
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
        return transaction {
            Products.update({ Products.id eq product.id }) {
                it[Products.name] = product.name;
                it[Products.description] = product.description;
                it[Products.price] = product.price;
            }
        }
    }

    fun deleteProduct(product: ProductDTO) {
        return transaction {
            Products.deleteWhere { Products.id eq product.id } > 0
        }
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