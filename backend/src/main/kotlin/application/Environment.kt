package application

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

fun buildEnvironment(): Environment {
    val config: Config = ConfigFactory.load("application.conf")
    return Environment(
        dbUrl = config.getString("ktor.db.dbUrl"),
        dbDriver = config.getString("ktor.db.dbDriver"),
        dbUser = config.getString("ktor.db.dbUser"),
        dbPwd = config.getString("ktor.db.dbPwd"),
        jwtSecret = config.getString("jwt.secret"),
        jwtIssuer = config.getString("jwt.issuer"),
        jwtAudience = config.getString("jwt.audience"),
        jwtExpiresAt = config.getInt("jwt.expiresAt"),
        jwtRealm = config.getString("jwt.realm"),
        encryptKey = config.getString("ktor.encryptKey"),
    )
}

data class Environment(
    var dbUrl: String,
    var dbDriver: String,
    var dbUser: String,
    var dbPwd: String,
    var jwtSecret: String = "",
    var jwtIssuer: String = "",
    var jwtAudience: String = "",
    var jwtExpiresAt: Int,
    var jwtRealm: String = "",
    var encryptKey: String = "",
)