package application

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

val moduleKoin = module {
    single { buildEnvironment() as Environment }


}
class Inject : KoinComponent {
    val environment by inject<Environment>()
}


