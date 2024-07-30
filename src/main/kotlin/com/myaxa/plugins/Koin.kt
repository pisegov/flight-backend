package com.myaxa.plugins

import com.myaxa.data.network_client.NetworkClient
import com.myaxa.data.network_client.provideHttpClient
import com.myaxa.features.lighting_scheduling.TimerLightingSwitcher
import io.ktor.client.*
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val microcontrollerUrl = environment.config
        .propertyOrNull("ktor.security.microcontroller.url")?.getString() ?: return

    val appModule = module {
        single<HttpClient> { provideHttpClient(microcontrollerUrl) }
        single { NetworkClient(get()) }
        factory { (coroutineScope: CoroutineScope) ->
            TimerLightingSwitcher(get(), coroutineScope)
        }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}