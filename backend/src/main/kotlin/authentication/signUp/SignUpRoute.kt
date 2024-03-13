package authentication.signUp

import application.json
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.ktor.ext.inject

fun Application.signUpRouting() {
    routing {
        signUp()
    }
}

fun Route.signUp() {
    val signUp: SignUp by inject()
    post("/signUp") {
        with(call) {
            val input = receive<JsonObject>()
            signUp(receive = input,
                success = {
                    respond(HttpStatusCode.OK)
                }, failure = {
                    respond(it.statusCode, json.encodeToJsonElement(it.errorMessage))
                }
            )
        }
    }
}
