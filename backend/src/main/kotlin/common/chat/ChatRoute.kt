package common.chat

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


fun Application.chatRouting() {
    routing {
        route("/chat") {
            authenticate("auth-jwt") {
                insertChat()
                updateChat()
                getChats()
                delete()
            }
        }
    }
}

fun Route.insertChat() {
    val chatHandler: ChatHandler by inject()
    post {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                val input = receive<JsonObject>()
                chatHandler.insertChat(input, principal).foldSuspendable(
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

fun Route.updateChat() {
    val chatHandler: ChatHandler by inject()
    put("{chatId}") {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                val chatId = parameters.getOrFail<Int>("chatId")
                val input = receive<JsonObject>()
                chatHandler.updateChat(input, chatId).foldSuspendable(
                    success = {
                        respond(HttpStatusCode.OK)
                    }, failure = {
                        respond(it.statusCode, json.encodeToJsonElement(it.errorMessage))
                    }
                )
            }
        }
    }
}

fun Route.getChats() {
    val chatHandler: ChatHandler by inject()
    get {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                chatHandler.getChats(principal).foldSuspendable(
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

fun Route.delete() {
    val chatHandler: ChatHandler by inject()
    delete("{chatId}") {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                val chatId = parameters.getOrFail<Int>("chatId")
                chatHandler.delete(principal, chatId).foldSuspendable(
                    success = {
                        respond(HttpStatusCode.OK)
                    }, failure = {
                        respond(it.statusCode, json.encodeToJsonElement(it.errorMessage))
                    }
                )
            }
        }
    }
}