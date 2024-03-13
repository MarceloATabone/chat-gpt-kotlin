package application

import authentication.signIn.SignIn
import authentication.signUp.SignUp
import common.chat.ChatRepository
import common.chat.ChatRepositoryImpl
import common.history.HistoryRepository
import common.history.HistoryRepositoryImpl
import common.user.UserRepository
import common.user.UserRepositoryImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import secret.SecretService
import kotlin.math.sin

val moduleKoin = module {
    single { buildEnvironment() as Environment }
    single { SecretService(get() as Environment) }
    single { UserRepositoryImpl() as UserRepository }
    single { ChatRepositoryImpl() as ChatRepository }
    single { HistoryRepositoryImpl() as HistoryRepository }
    single { SignIn(get() as Environment, get() as SecretService, get() as UserRepository) }
    single { SignUp(get() as SecretService, get() as UserRepository) }
}

class Inject : KoinComponent {
    val environment by inject<Environment>()
}


