package common.chat

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Int? = null,
    val userId: Int,
    var name: String,
    val createAt: String = "",
    val updateAt: String = ""
)