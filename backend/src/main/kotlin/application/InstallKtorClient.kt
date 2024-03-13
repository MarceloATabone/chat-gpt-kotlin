package application

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun ktorClient(): HttpClient {
    return HttpClient() {
        install(ContentNegotiation) {
            json(json)
        }
    }
}