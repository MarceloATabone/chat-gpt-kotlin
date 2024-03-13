package gpt

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.json.JsonObject

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
   // val gpt: Gpt by inject()
    post("{chatId}") {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                val chatId = parameters.getOrFail<Int>("chatId")
                val receive = receive<JsonObject>()

            }
        }
    }
}
