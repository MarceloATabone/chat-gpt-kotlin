package common.history

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


fun Application.historyRouting() {
    routing {
        route("/history") {
            authenticate("auth-jwt") {
                insertHistory()
                getHistory()
            }
        }
    }
}

fun Route.insertHistory() {
    val historyHandler: HistoryHandler by inject()
    post {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                val input = receive<JsonObject>()
                historyHandler.insertHistory(input, principal).foldSuspendable(
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

fun Route.getHistory() {
    val historyHandler: HistoryHandler by inject()
    get("{chatId}") {
        with(call) {
            val principal = principal<JWTPrincipal>()
            principal?.let {
                val chatId = parameters.getOrFail<Int>("chatId")
                historyHandler.getHistory(principal, chatId).foldSuspendable(
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