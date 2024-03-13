package authentication.signUp

import common.user.User
import common.user.UserRepository
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import secret.SecretService
import util.Either
import util.ErrorMessage
import util.Error

class SignUp(
    private val secretService: SecretService,
    private val userRepository: UserRepository,
) {
    private data class Content(
        val email: String,
        val name: String,
        val password: String,
        val passwordVerify: String
    )

    suspend operator fun invoke(
        receive: JsonObject,
        success: suspend () -> Unit,
        failure: suspend (Error) -> Unit
    ) {
        createContent(receive).foldSuspendable(
            failure = { failure(it) }
        ) { content ->
            insertUser(createUser(content)).foldSuspendable(failure) { user ->
                success()
            }
        }
    }

    private suspend fun createContent(receive: JsonObject): Either<Error, Content> {
        var content: Content
        val email = receive["email"]?.jsonPrimitive?.contentOrNull
        val password = receive["password"]?.jsonPrimitive?.contentOrNull
        val passwordVerify = receive["passwordVerify"]?.jsonPrimitive?.contentOrNull
        val name = receive["name"]?.jsonPrimitive?.contentOrNull

        return if (email != null && password != null && passwordVerify != null && name != null) {
            verifyEmail(email).foldSuspendable(
                failure = { Either.Failure(it) }
            ) {
                content = Content(email, name, password, passwordVerify)
                Either.Success(content)
            }
        } else {
            Either.Failure(Error(HttpStatusCode.BadRequest, ErrorMessage("The received JSON is invalid.")))
        }
    }

    private suspend fun createUser(content: Content): Either<Error, User> {
        return secretService.encrypt(content.password, content.passwordVerify).foldSuspendable(
            failure = { Either.Failure(it) }
        ) { encryptedPassword ->
            Either.Success(
                User(
                    isActive = true,
                    email = content.email,
                    name = content.name,
                    password = encryptedPassword,
                )
            )
        }
    }

    private suspend fun insertUser(result: Either<Error, User>): Either<Error, User> {
        return result.foldSuspendable(
            failure = { errorMessage ->
                Either.Failure(errorMessage)
            }, success = { user ->
                userRepository.insert(user).fold(
                    failure = { errorMessage ->
                        Either.Failure(errorMessage)
                    },
                    success = { insertResult ->
                        Either.Success(insertResult)
                    }
                )
            }
        )
    }

    private suspend fun verifyEmail(email: String): Either<Error, Boolean> {
        val emailRegexPattern = Regex(pattern = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return if (email.matches(emailRegexPattern)) {
            userRepository.verifyEmail(email).foldSuspendable(
                failure = { Either.Failure(it) },
                success = { Either.Success(true) }
            )
        } else {
            Either.Failure(
                Error(
                    HttpStatusCode.Conflict,
                    ErrorMessage("The email sent does not match the valid email address.")
                )
            )
        }
    }

}