package application

import authentication.signIn.signInRouting
import authentication.signUp.signUpRouting
import io.ktor.server.application.*

fun Application.routing() {
    signInRouting()
    signUpRouting()
}