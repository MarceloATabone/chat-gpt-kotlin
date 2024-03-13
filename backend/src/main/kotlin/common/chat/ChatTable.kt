package common.chat

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ChatTable : Table() {
    var id = integer("id").autoIncrement()
    var userId = integer("user_id")
    var name = varchar("name", 255)
    val createAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}
