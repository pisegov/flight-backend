package com.myaxa.utils

import kotlinx.coroutines.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Scheduler(private val task: suspend () -> Unit) {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val coroutineScope = CoroutineScope(Dispatchers.Default.limitedParallelism(1))

    fun scheduleExecution(every: Every) {
        coroutineScope.launch {
            while (true) {
                task()
                delay(every.n.toDuration(every.unit))
            }
        }
    }

    fun stop() {
        coroutineScope.coroutineContext.job.cancel()
    }
}

data class Every(val n: Long, val unit: DurationUnit)