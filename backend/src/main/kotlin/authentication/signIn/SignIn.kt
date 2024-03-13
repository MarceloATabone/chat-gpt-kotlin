package authentication.signIn

import application.Environment
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import common.user.User
import common.user.UserRepository
import secret.SecretService
import util.Either
import java.util.*
import util.Error

class SignIn(
    private val environment: Environment,
    private val secretService: SecretService,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        user: User,
        success: suspend (String) -> Unit,
        failure: suspend (Error) -> Unit
    ) {
        verifyUser(user).foldSuspendable(failure) { returnUser ->
            success(createBearerToken(environment, returnUser))
        }
    }

    private suspend fun verifyUser(user: User): Either<Error, User> {
        return getUser(user).foldSuspendable(
            failure = { Either.Failure(it) }
        ) { returnUser ->
            secretService.verifyPassword(user.password, returnUser.password).foldSuspendable(
                failure = { Either.Failure(it) }
            ) {
                Either.Success(returnUser)
            }
        }
    }

    private suspend fun getUser(user: User): Either<Error, User> {
        return userRepository.getByEmail(user.email).foldSuspendable(
            success = { Either.Success(it) },
            failure = { Either.Failure(it) }
        )
    }

    private fun createBearerToken(environment: Environment, user: User): String {
        val token = JWT.create()
            .withAudience(environment.jwtIssuer)
            .withIssuer(environment.jwtIssuer)
            .withClaim("email", user.email)
            .withExpiresAt(Date(System.currentTimeMillis() + environment.jwtExpiresAt))
            .sign(Algorithm.HMAC256(environment.jwtSecret))
        return "Bearer $token"
    }

}