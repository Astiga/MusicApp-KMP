package musicapp.di

import org.koin.core.context.startKoin

object KoinHelper {
    fun initKoin() {
        stopKoin()
        startKoin {
            modules(commonModule, iosModule)
        }
    }

    fun stopKoin() {
        stopKoin()
    }
}