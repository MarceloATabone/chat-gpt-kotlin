package application

import authentication.signIn.signInRouting
import authentication.signUp.signUpRouting
import common.chat.chatRouting
import io.ktor.server.application.*

fun Application.routing() {
    signInRouting()
    signUpRouting()
    chatRouting()
}