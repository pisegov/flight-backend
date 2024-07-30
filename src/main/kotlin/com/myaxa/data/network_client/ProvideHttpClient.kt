package com.myaxa.data.network_client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.http.*

fun provideHttpClient(baseUrl: String): HttpClient {
    return HttpClient(CIO).config {
        defaultRequest { url(baseUrl) }

        install(HttpRequestRetry) {
            maxRetries = 5
            retryIf { request, response ->
                !response.status.isSuccess()
            }
            exponentialDelay()
        }
    }
}
