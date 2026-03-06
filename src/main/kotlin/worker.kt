package com.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.channels.Channel

data class EmailMessage(val to: String, val subject: String, val body: String)

val emailQueue = Channel<EmailMessage>(Channel.UNLIMITED)

fun startEmailWorker() {
    CoroutineScope(Dispatchers.IO).launch {
        for (msg in emailQueue) {
            try {
                fakeSendEmail(msg)
            } catch (_: Exception) {

            }
        }
    }
}

fun fakeSendEmail(msg: EmailMessage) {
    // Тот самый фейковый емаил
    println("[FAKE EMAIL] To: ${msg.to}, Subject: ${msg.subject}, Body: ${msg.body}")
}
