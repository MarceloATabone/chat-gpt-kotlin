package common.history

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object HistoryTable : Table() {
    var id = integer("id").autoIncrement()
    var chatId = integer("chat_id")
    var userMsg = bool("user_msg").default(false)
    var value = varchar("value", 10024)
    val createAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}

