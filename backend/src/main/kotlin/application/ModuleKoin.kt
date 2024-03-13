package application

import authentication.signIn.SignIn
import authentication.signUp.SignUp
import common.chat.ChatHandler
import common.chat.ChatRepository
import common.chat.ChatRepositoryImpl
import common.history.HistoryHandler
import common.history.HistoryRepository
import common.history.HistoryRepositoryImpl
import common.user.UserRepository
import common.user.UserRepositoryImpl
import gpt.Gpt
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import secret.SecretService

val moduleKoin = module {
    single { buildEnvironment() as Environment }
    single { SecretService(get() as Environment) }
    single { UserRepositoryImpl() as UserRepository }
    single { ChatRepositoryImpl() as ChatRepository }
    single { HistoryRepositoryImpl() as HistoryRepository }
    single { ChatHandler(get() as UserRepository, get() as ChatRepository, get() as HistoryRepository) }
    single { HistoryHandler(get() as HistoryRepository, get() as ChatHandler) }
    single { SignIn(get() as Environment, get() as SecretService, get() as UserRepository) }
    single { SignUp(get() as SecretService, get() as UserRepository) }
    single { Gpt(get() as Environment, get() as ChatHandler, get() as HistoryRepository) }
}

class Inject : KoinComponent {
    val environment by inject<Environment>()
}


