package common.history

import kotlinx.serialization.Serializable

@Serializable
data class History(
    val id: Int? = null,
    val chatId: Int,
    val userMsg: Boolean = false,
    val value: String = "",
    val createAt: String = "",
    val updateAt: String = "",
)