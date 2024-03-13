package common.user

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UserTable : Table() {
    var id = integer("id").autoIncrement()
    var email = varchar("email", 255)
    var password = varchar("password", 255)
    var name = varchar("name", 255)
    var isActive = bool("is_active").default(false)
    val createAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}