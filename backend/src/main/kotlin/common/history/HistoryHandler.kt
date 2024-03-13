package common.history

import common.chat.ChatHandler
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.json.*
import util.Either
import util.Error
import util.ErrorMessage

class HistoryHandler(
    private val historyRepository: HistoryRepository,
    private val chatHandler: ChatHandler
) {
    suspend fun verifyOwner(principal: JWTPrincipal, historyId: Int): Either<Error, History> {
        return historyRepository.get(historyId).foldSuspendable(
            failure = { Either.Failure(it) }
        ) { history ->
            chatHandler.verifyOwner(principal, history.chatId).foldSuspendable(
                failure = { Either.Failure(it) },
                success = { Either.Success(history) }
            )
        }
    }

    suspend fun insertHistory(receive: JsonObject, principal: JWTPrincipal): Either<Error, Boolean> {
        return createHistory(receive, principal).foldSuspendable(
            failure = { Either.Failure(it) }
        ) { history ->
            historyRepository.insert(history).foldSuspendable(
                failure = { Either.Failure(it) },
                success = { Either.Success(it) }
            )
        }
    }

    suspend fun getHistory(principal: JWTPrincipal, chatId: Int): Either<Error, List<History>> {
        return chatHandler.verifyOwner(principal, chatId).foldSuspendable(
            failure = { Either.Failure(it) }
        ) {
            historyRepository.getByChatId(chatId).foldSuspendable(
                failure = { Either.Failure(it) }
            ) { histories ->
                Either.Success(histories)
            }
        }
    }

    private suspend fun createHistory(receive: JsonObject, principal: JWTPrincipal): Either<Error, History> {
        val chatId = receive["chatId"]?.jsonPrimitive?.intOrNull
        val userMsg = receive["userMsg"]?.jsonPrimitive?.booleanOrNull
        val value = receive["value"]?.jsonPrimitive?.contentOrNull

        return if (chatId != null && value != null && userMsg != null) {
            chatHandler.verifyOwner(principal, chatId).foldSuspendable(
                failure = { Either.Failure(it) }
            ) {
                Either.Success(
                    History(
                        chatId = chatId,
                        userMsg = userMsg,
                        value = value
                    )
                )
            }
        } else {
            Either.Failure(Error(HttpStatusCode.BadRequest, ErrorMessage("The received JSON is invalid.")))
        }
    }

}