package common.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isActive: Boolean = false,
    val createAt: String = "",
    val updateAt: String = ""
)