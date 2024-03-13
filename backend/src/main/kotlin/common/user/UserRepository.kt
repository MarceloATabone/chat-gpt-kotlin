package common.user

import application.DataBase.dbQuery
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import util.Either
import util.Formatting
import util.Error
import util.ErrorMessage

class UserRepositoryImpl : UserRepository {
    private fun ResultRow.toUser() = User(
        id = this[UserTable.id],
        email = this[UserTable.email],
        password = this[UserTable.password],
        name = this[UserTable.name],
        isActive = this[UserTable.isActive],
        createAt = this[UserTable.createAt].format(Formatting.formatterISO8601),
        updateAt = this[UserTable.updatedAt].format(Formatting.formatterISO8601)
    )

    override suspend fun verifyEmail(email: String): Either<Error, Boolean> = dbQuery {
        val exist = UserTable.select { UserTable.email.eq(email) }.empty()
        if (exist) return@dbQuery Either.Success(true)
        else return@dbQuery Either.Failure(
            Error(
                HttpStatusCode.Conflict,
                ErrorMessage("This email is already registered.")
            )
        )
    }

    override suspend fun get(principal: JWTPrincipal): Either<Error, User> = dbQuery {
        val user = principal.payload.getClaim("email").asString()
        this.getByEmail(user)
    }

    override suspend fun getByEmail(email: String): Either<Error, User> = dbQuery {
        UserTable.select { UserTable.email.eq(email) }.limit(1).singleOrNull()?.toUser()
            ?.let { Either.Success(it) } ?: Either.Failure(
            Error(
                HttpStatusCode.NotFound,
                ErrorMessage("User By email not found")
            )
        )
    }

    override suspend fun getById(id: Int): Either<Error, User> = dbQuery {
        UserTable.select { UserTable.id.eq(id) }.limit(1).singleOrNull()?.toUser()
            ?.let { Either.Success(it) } ?: Either.Failure(
            Error(
                HttpStatusCode.NotFound,
                ErrorMessage("User ID not found")
            )
        )
    }

    override suspend fun insert(user: User): Either<Error, User> = dbQuery {
        when {
            UserTable.select { UserTable.email eq user.email }.count() == 0L -> {
                UserTable.insert {
                    it[email] = user.email
                    it[password] = user.password
                    it[name] = user.name
                }.resultedValues?.get(0)?.toUser()?.let { Either.Success(it) }
                    ?: Either.Failure(Error(HttpStatusCode.NotFound, ErrorMessage("Error in insert")))
            }

            else -> Either.Failure(Error(HttpStatusCode.NotFound, ErrorMessage("The user is already registered.")))
        }
    }

}

interface UserRepository {
    suspend fun verifyEmail(email: String): Either<Error, Boolean>
    suspend fun get(principal: JWTPrincipal): Either<Error, User>
    suspend fun getByEmail(email: String): Either<Error, User>
    suspend fun getById(id: Int): Either<Error, User>
    suspend fun insert(user: User): Either<Error, User>
}