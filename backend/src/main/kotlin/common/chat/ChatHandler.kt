package common.chat

import common.history.HistoryRepository
import common.user.UserRepository
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import util.Either
import util.Error
import util.ErrorMessage

class ChatHandler(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val historyRepository: HistoryRepository
) {

    suspend fun verifyOwner(principal: JWTPrincipal, chatId: Int): Either<Error, Chat> {
        return userRepository.get(principal).foldSuspendable(
            failure = { Either.Failure(it) }
        ) { user ->
            chatRepository.get(chatId).foldSuspendable(
                failure = { Either.Failure(it) }
            ) { chat ->
                if (chat.userId == user.id) {
                    Either.Success(chat)
                } else {
                    Either.Failure(Error(HttpStatusCode.Conflict, ErrorMessage("This chat is not from this user")))
                }
            }
        }
    }

    suspend fun insertChat(receive: JsonObject, principal: JWTPrincipal): Either<Error, Chat> {
        return userRepository.get(principal).foldSuspendable(
            failure = { Either.Failure(it) }
        ) { user ->
            if (user.id != null) {
                createChat(receive, user.id).foldSuspendable(
                    failure = { Either.Failure(it) }
                ) { chat ->
                    chatRepository.insert(chat).foldSuspendable(
                        failure = { Either.Failure(it) }
                    ) { insertedChat ->
                        Either.Success(insertedChat)
                    }
                }
            } else {
                Either.Failure(
                    Error(
                        HttpStatusCode.InternalServerError,
                        ErrorMessage("Error in get user in insert chat")
                    )
                )
            }
        }
    }

    suspend fun getChats(principal: JWTPrincipal): Either<Error, List<Chat>> {
        return userRepository.get(principal).foldSuspendable(
            failure = { Either.Failure(it) }
        ) { user ->
            if (user.id != null) {
                chatRepository.getByUserId(user.id).foldSuspendable(
                    failure = { Either.Failure(it) },
                    success = { Either.Success(it) }
                )
            } else {
                Either.Failure(
                    Error(
                        HttpStatusCode.InternalServerError,
                        ErrorMessage("Error in get user in insert chat")
                    )
                )

            }
        }
    }

    suspend fun updateChat(receive: JsonObject, chatId: Int): Either<Error, Boolean> {
        val name = receive["name"]?.jsonPrimitive?.contentOrNull
        return if (name != null) {
            chatRepository.get(chatId).foldSuspendable(
                failure = { Either.Failure(it) }
            ) { chat ->
                chat.name = name
                chatRepository.update(chat).foldSuspendable(
                    failure = { Either.Failure(it) }
                ) {
                    Either.Success(it)
                }
            }
        } else {
            Either.Failure(Error(HttpStatusCode.BadRequest, ErrorMessage("The received JSON is invalid.")))
        }
    }

    suspend fun delete(principal: JWTPrincipal, chatId: Int): Either<Error, Boolean> {
        return verifyOwner(principal, chatId).foldSuspendable(
            failure = { Either.Failure(it) }
        ) {
            historyRepository.deleteAll(chatId).foldSuspendable(
                failure = { return@foldSuspendable Either.Failure(it) }
            ) {
                chatRepository.delete(chatId).foldSuspendable(
                    failure = { return@foldSuspendable Either.Failure(it) },
                    success = { return@foldSuspendable Either.Success(it) }
                )
            }
        }
    }

    private fun createChat(receive: JsonObject, userId: Int): Either<Error, Chat> {
        val name = receive["name"]?.jsonPrimitive?.contentOrNull
        return if (name != null) {
            Either.Success(Chat(name = name, userId = userId))
        } else {
            Either.Failure(Error(HttpStatusCode.BadRequest, ErrorMessage("The received JSON is invalid.")))
        }
    }

}