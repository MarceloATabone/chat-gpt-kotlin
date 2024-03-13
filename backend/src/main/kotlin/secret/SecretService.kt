package secret

import application.Environment
import com.nexus.domain.secret.SecretRules
import io.ktor.http.*
import org.mindrot.jbcrypt.BCrypt
import util.Either
import util.Error
import util.ErrorMessage

class SecretService(
    val environment: Environment
) {
    suspend fun encrypt(secret: String, secretVerify: String): Either<Error, String> {
        return verifyMath(secret, secretVerify).foldSuspendable(
            failure = { Either.Failure(it) }
        ) {
            verifySize(secret).foldSuspendable(
                failure = { Either.Failure(it) },
            ) {
                verifyForbiddenChars(secret).foldSuspendable(
                    failure = { Either.Failure(it) }
                ) {
                    verifyUppercase(secret).foldSuspendable(
                        failure = { Either.Failure(it) }
                    ) {
                        verifySpecialCharacter(secret).foldSuspendable(
                            failure = { Either.Failure(it) }
                        ) {
                            val encryptPassword = hashPassword(plusEncryptKey(secret))
                            Either.Success(encryptPassword)
                        }
                    }
                }
            }
        }
    }

    private fun hashPassword(secret: String): String {
        return BCrypt.hashpw(secret, BCrypt.gensalt())
    }

    private fun plusEncryptKey(secret: String): String {
        return secret + environment.encryptKey
    }

    fun verifyPassword(secret: String, hashedSecret: String): Either<Error, Boolean> {
        return if (BCrypt.checkpw(plusEncryptKey(secret), hashedSecret)) Either.Success(true)
        else Either.Failure(Error(HttpStatusCode.Conflict, ErrorMessage("Password do not match")))
    }

    private fun verifyMath(secret: String, secretVerify: String): Either<Error, Boolean> {
        return if (secret != secretVerify) Either.Failure(
            Error(
                HttpStatusCode.Conflict,
                ErrorMessage("Secrets do not match")
            )
        )
        else Either.Success(true)
    }

    private fun verifySize(secret: String): Either<Error, Boolean> {
        return if (secret.length < (SecretRules.Size.rule as Int) || secret.length > (SecretRules.MaxSize.rule as Int))
            Either.Failure(
                Error(
                    HttpStatusCode.Forbidden,
                    ErrorMessage("Password must have at least ${SecretRules.Size.rule} characters and less than ${SecretRules.MaxSize.rule}")
                )
            )
        else Either.Success(true)
    }

    private fun verifyForbiddenChars(secret: String): Either<Error, Boolean> {
        val forbiddenChars = (SecretRules.WithoutChars.rule as String).toSet()
        return if (secret.any { it in forbiddenChars }) Either.Failure(
            Error(
                HttpStatusCode.Forbidden,
                ErrorMessage(
                    "Password contains invalid characters: '${SecretRules.WithoutChars.rule}'"
                )
            )
        )
        else Either.Success(true)
    }

    private fun verifyUppercase(secret: String): Either<Error, Boolean> {
        // Check for at least one uppercase letter
        return if (!secret.any { it.isUpperCase() }) Either.Failure(
            Error(
                HttpStatusCode.Forbidden,
                ErrorMessage("Password must contain at least one uppercase letter")
            )
        )
        else Either.Success(true)
    }

    private fun verifySpecialCharacter(secret: String): Either<Error, Boolean> {
        // Check for at least one special character
        val specialCharacters = (SecretRules.MustHaveChar.rule as String).toSet()
        return if (!secret.any { it in specialCharacters }) Either.Failure(
            Error(
                HttpStatusCode.Forbidden,
                ErrorMessage(
                    "Password must contain at least one special character: '${SecretRules.MustHaveChar.rule}'"
                )
            )
        )
        else Either.Success(true)
    }
}