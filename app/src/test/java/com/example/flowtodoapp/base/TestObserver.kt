package com.example.flowtodoapp.base

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

fun <T> Flow<T>.test(scope: CoroutineScope): TestObserver<T> =
    TestObserver(scope, this)

class TestObserver<T>(
    scope: CoroutineScope,
    flow: Flow<T>
) {

    private val values = mutableListOf<T>()
    private val job: Job = scope.launch {
        flow.toList(values)
    }

    fun assertNoValues(): TestObserver<T> {
        assertEquals(emptyList<T>(), this.values)
        return this
    }

    fun assertValues(vararg values: T): TestObserver<T> {
        assertEquals(values.toList(), this.values)
        return this
    }

    fun finish() {
        job.cancel()
    }
}