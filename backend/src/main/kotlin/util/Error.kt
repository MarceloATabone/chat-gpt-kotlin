package util

import io.ktor.http.*

data class Error(val statusCode: HttpStatusCode, val message: String)
