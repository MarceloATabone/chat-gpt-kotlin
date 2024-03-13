package gpt

import kotlinx.serialization.Serializable


@Serializable
data class ReturnMessage(
    val message: String
)

@Serializable
data class OpenAIResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Float
)

@Serializable
data class Message(
    val role: String? = "",
    val content: String
)