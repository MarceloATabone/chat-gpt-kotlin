package application

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.StdOutSqlLogger


object DataBase {
    private fun database() = Database.connect(
        injecAt.environment.dbUrl,
        user = injecAt.environment.dbUser,
        driver = injecAt.environment.dbDriver,
        password = injecAt.environment.dbPwd
    )

    fun init() {
        transaction(database()) {
            addLogger(StdOutSqlLogger)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}