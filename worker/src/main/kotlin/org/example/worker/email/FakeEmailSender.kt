package org.example.worker.email

class FakeEmailSender {
    fun send(to: String, subject: String, body: String) {
        println("Fake email to=$to subject=$subject body=$body")
    }
}
