package com.paulohenrique.poketeam.util

import kotlin.coroutines.cancellation.CancellationException

/** Como runCatching, mas sem engolir o cancelamento de corrotinas. */
suspend inline fun <T> safeCall(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
