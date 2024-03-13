package util

import io.ktor.http.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

data class Error(@Contextual val statusCode: HttpStatusCode, val errorMessage: ErrorMessage)
@Serializable
data class ErrorMessage(val message: String)