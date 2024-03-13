package gpt

import application.json
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.ktor.ext.inject

fun Application.gptRouting() {
    routing {
        route("/gpt") {
            authenticate("auth-jwt") {
                gpt()
            }
        }
    }
}

fun Route.gpt() {
    val gpt: Gpt by inject()
    post("{chatId}") {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                val chatId = parameters.getOrFail<Int>("chatId")
                val receive = receive<JsonObject>()
                gpt.chat(chatId, receive, principal,
                    success = {
                        respond(HttpStatusCode.OK, json.encodeToJsonElement(it))
                    }, failure = {
                        respond(it.statusCode, json.encodeToJsonElement(it.errorMessage))
                    }
                )
            }
        }
    }
}
