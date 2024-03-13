package authentication.signIn

import application.json
import common.user.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlinx.serialization.json.encodeToJsonElement

fun Application.signInRouting() {
    routing {
        signIn()
    }
}

fun Route.signIn() {
    val signIn: SignIn by inject()
    post("/signIn") {
        with(call) {
            val user = receive<User>()
            signIn(user = user,
                success = {
                    run {
                        response.header(HttpHeaders.Authorization, it)
                        respond(HttpStatusCode.OK)
                    }
                }, failure = {
                    respond(it.statusCode, json.encodeToJsonElement(it.errorMessage))
                }
            )
        }
    }
}