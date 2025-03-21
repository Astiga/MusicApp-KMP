package musicapp.di

import org.koin.core.context.startKoin

object KoinHelper {
    fun initKoin() {
        startKoin {
            modules(commonModule, iosModule)
        }
    }
}