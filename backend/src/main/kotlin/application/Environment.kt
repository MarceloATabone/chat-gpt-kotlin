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
        jwtSecret = config.getString("ktor.encryptKey"),
        jwtIssuer = config.getString("jwt.secret"),
        jwtAudience = config.getString("jwt.issuer"),
        jwtExpiresAt = config.getInt("jwt.audience"),
        jwtRealm = config.getString("jwt.expiresAt"),
        encryptKey = config.getString("jwt.realm"),
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