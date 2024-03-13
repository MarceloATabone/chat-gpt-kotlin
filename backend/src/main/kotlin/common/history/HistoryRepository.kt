package common.history

import application.DataBase.dbQuery
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import util.Either
import util.Error
import util.Formatting
import util.validateIfSQLOperationSucceeded

class HistoryRepositoryImpl : HistoryRepository {
    private fun ResultRow.toHistory() = History(
        id = this[HistoryTable.id],
        chatId = this[HistoryTable.chatId],
        userMsg = this[HistoryTable.userMsg],
        value = this[HistoryTable.value],
        createAt = this[HistoryTable.createAt].format(Formatting.formatterISO8601),
        updateAt = this[HistoryTable.updatedAt].format(Formatting.formatterISO8601)
    )

    override suspend fun get(id: Int): Either<Error, History> = dbQuery {
        HistoryTable.select { HistoryTable.id.eq(id) }.limit(1).singleOrNull()?.toHistory()
            ?.let { Either.Success(it) } ?: Either.Failure(Error(HttpStatusCode.NotFound, "No History found by ID"))
    }

    override suspend fun getByChatId(chatId: Int): Either<Error, List<History>> = dbQuery {
        val count = HistoryTable.select(HistoryTable.chatId.eq(chatId)).count()
        if (count > 0) {
            val history =
                HistoryTable.select(HistoryTable.chatId.eq(chatId)).orderBy(HistoryTable.createAt to SortOrder.DESC)
                    .map { it.toHistory() }
            Either.Success(history)
        } else {
            Either.Success(emptyList())
        }
    }

    override suspend fun insert(history: History): Either<Error, Boolean> = dbQuery {
        HistoryTable.insert {
            it[chatId] = history.chatId
            it[value] = history.value
            it[userMsg] = history.userMsg
        }.resultedValues?.get(0)?.toHistory()?.let { Either.Success(true) }
            ?: Either.Failure((Error(HttpStatusCode.InternalServerError, "Failed to insert History.")))
    }

    override suspend fun deleteAll(chatId: Int): Either<Error, Boolean> = dbQuery {
        val delete = HistoryTable.deleteWhere { HistoryTable.chatId eq chatId }.validateIfSQLOperationSucceeded()
        Either.Success(true)
        // else Either.Failure(ErrorMessage.HistoryDelete)
    }

}

interface HistoryRepository {
    suspend fun get(id: Int): Either<Error, History>
    suspend fun getByChatId(chatId: Int): Either<Error, List<History>>
    suspend fun insert(history: History): Either<Error, Boolean>
    suspend fun deleteAll(userId: Int): Either<Error, Boolean>
}