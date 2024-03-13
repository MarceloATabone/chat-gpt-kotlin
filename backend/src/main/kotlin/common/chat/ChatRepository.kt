package common.chat


import application.DataBase.dbQuery
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import util.*

class ChatRepositoryImpl : ChatRepository {
    private fun ResultRow.toChat() = Chat(
        id = this[ChatTable.id],
        userId = this[ChatTable.userId],
        name = this[ChatTable.name],
        createAt = this[ChatTable.createAt].format(Formatting.formatterISO8601),
        updateAt = this[ChatTable.updatedAt].format(Formatting.formatterISO8601)
    )

    override suspend fun get(id: Int): Either<Error, Chat> = dbQuery {
        ChatTable.select { ChatTable.id.eq(id) }.limit(1).singleOrNull()?.toChat()
            ?.let { Either.Success(it) } ?: Either.Failure(
            Error(
                HttpStatusCode.NotFound,
                ErrorMessage("No chat found by ID")
            )
        )
    }

    override suspend fun getByUserId(userId: Int): Either<Error, List<Chat>> = dbQuery {
        val count = ChatTable.select(ChatTable.userId.eq(userId)).count()
        if (count > 0) {
            val chats = ChatTable.select(ChatTable.userId.eq(userId)).orderBy(ChatTable.createAt to SortOrder.DESC)
                .map { it.toChat() }
            Either.Success(chats)
        } else {
            Either.Success(emptyList())
        }
    }

    override suspend fun insert(chat: Chat): Either<Error, Chat> = dbQuery {
        ChatTable.insert {
            it[name] = chat.name
            it[userId] = chat.userId
        }.resultedValues?.get(0)?.toChat()?.let { Either.Success(it) }
            ?: Either.Failure(Error(HttpStatusCode.InternalServerError, ErrorMessage("Failed to insert chat.")))
    }

    override suspend fun update(chat: Chat): Either<Error, Boolean> = dbQuery {
        if (chat.id != null) {
            val updated = ChatTable.update({ ChatTable.id.eq(chat.id) }) {
                it[name] = chat.name
            }.validateIfSQLOperationSucceeded()
            if (updated) Either.Success(true)
            else Either.Failure(Error(HttpStatusCode.InternalServerError, ErrorMessage("Failed to update chat.")))
        } else {
            Either.Failure(Error(HttpStatusCode.NotFound, ErrorMessage("No chat found by ID in Insert")))
        }
    }

    override suspend fun delete(chatId: Int): Either<Error, Boolean> = dbQuery {
        val delete = ChatTable.deleteWhere { ChatTable.id eq chatId }.validateIfSQLOperationSucceeded()
        if (delete) Either.Success(true)
        else Either.Failure(Error(HttpStatusCode.InternalServerError, ErrorMessage("Failed to delete chat.")))
    }

}

interface ChatRepository {
    suspend fun get(id: Int): Either<Error, Chat>
    suspend fun getByUserId(userId: Int): Either<Error, List<Chat>>
    suspend fun insert(chat: Chat): Either<Error, Chat>
    suspend fun update(chat: Chat): Either<Error, Boolean>
    suspend fun delete(chatId: Int): Either<Error, Boolean>
}