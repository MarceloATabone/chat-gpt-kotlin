package application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.installExtensions() {
    install(Koin) {
        slf4jLogger()
        modules(moduleKoin)
    }

    DataBase.init()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = injecAt.environment.jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(injecAt.environment.jwtSecret))
                    .withAudience(injecAt.environment.jwtAudience)
                    .withIssuer(injecAt.environment.jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Bearer Not Valid")
            }
        }
    }

    install(ContentNegotiation) {
        json(
            contentType = ContentType.Application.Json,
            json = json
        )
    }

    install(CORS) {
        anyHost()
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true

        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.AccessControlExposeHeaders)
        allowHeader(HttpHeaders.Authorization)

        exposeHeader(HttpHeaders.Authorization)
    }

    install(Resources)
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openApi/documentation.yaml")
    }
}