package gpt

import application.Environment
import application.httpClient
import application.json
import common.chat.ChatHandler
import common.history.History
import common.history.HistoryRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import util.Either
import util.Error
import util.ErrorMessage

class Gpt(
    private val environment: Environment,
    private val chatHandler: ChatHandler,
    private val historyRepository: HistoryRepository,
) {

    suspend fun chat(
        chatId: Int,
        receive: JsonObject,
        principal: JWTPrincipal,
        success: suspend (ReturnMessage) -> Unit,
        failure: suspend (Error) -> Unit
    ) {

        var instruction = ""

        chatHandler.verifyOwner(principal, chatId).foldSuspendable(
            failure = { failure(it) }
        ) { chat ->
            verifyMessage(receive).foldSuspendable(
                failure = { failure(it) }
            ) { userMessage ->
                val messagesForRequest = createChatHistory(chatId, userMessage, instruction)
                sendRequest(messagesForRequest).foldSuspendable(
                    failure = { Either.Failure(it) },
                    success = { gptResponse ->
                        historyRepository.insert(
                            History(
                                chatId = chatId,
                                userMsg = true,
                                value = userMessage
                            )
                        )
                        historyRepository.insert(
                            History(
                                chatId = chatId,
                                userMsg = false,
                                value = gptResponse
                            )
                        )
                        success(ReturnMessage(gptResponse))
                    }
                )
            }
        }
    }

    private suspend fun sendRequest(
        messages: List<Message>
    ): Either<Error, String> {
        val openAIRequest = OpenAIRequest(
            model = environment.gptModel,
            messages = messages,
            temperature = 0.5f
        )
        val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${environment.gptKey}")
            setBody(json.encodeToJsonElement(openAIRequest))
        }

        return if (response.status.isSuccess()) {
            val responseBody = response.body<String>()
            val openAIResponse = json.decodeFromString<OpenAIResponse>(responseBody)
            val gptMessage = openAIResponse.choices.firstOrNull()?.message?.content
            if (gptMessage != null) {
                Either.Success(gptMessage)
            } else Either.Failure(
                Error(
                    HttpStatusCode.InternalServerError,
                    ErrorMessage("Failed to get a successful response from GPT")
                )
            )
        } else {
            when (response.status) {
                HttpStatusCode.TooManyRequests -> Either.Failure(
                    Error(
                        HttpStatusCode.InternalServerError,
                        ErrorMessage("Limit of requests reached to gpt")
                    )
                )

                else -> {
                    Either.Failure(
                        Error(
                            HttpStatusCode.InternalServerError,
                            ErrorMessage("Failed to get a successful response from GPT")
                        )
                    )
                }
            }
        }
    }

    private suspend fun createChatHistory(
        chatId: Int,
        userMessage: String,
        instruction: String
    ): List<Message> {
        val historyMessages = historyRepository.getByChatId(chatId).foldSuspendable(
            failure = { listOf() },
            success = { it }
        )
        return mutableListOf<Message>().apply {
            add(Message(role = "system", content = instruction))
            historyMessages.forEach { history ->
                add(Message(role = if (history.userMsg) "user" else "assistant", content = history.value))
            }
            add(Message(role = "user", content = userMessage))
        }
    }

    private fun verifyMessage(receive: JsonObject): Either<Error, String> {
        val message = receive["message"]?.jsonPrimitive?.contentOrNull
        return if (message != "" && message != null) Either.Success(message.toString())
        else Either.Failure(Error(HttpStatusCode.BadRequest, ErrorMessage("the message is null or is empty")))
    }


}