package com.example.flowtodoapp.base

import com.example.flowtodoapp.scenes.counter.IncrementCounterMiddleware
import com.example.flowtodoapp.scenes.model.CounterAction
import com.example.flowtodoapp.scenes.model.CounterState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.KoinTest

@FlowPreview
@ExperimentalCoroutinesApi
class IncrementCounterMiddlewareTest : KoinTest {

    @Test
    fun `bind() should increment counter`() {
        val actionFlow = flow { emit(CounterAction.Increment()) }
        val stateFlow = flow { emit(CounterState(0)) }
        runBlockingTest {
            IncrementCounterMiddleware()
                .bind(actionFlow, stateFlow)
                .test(this)
                .assertValues(CounterAction.ShowValue(1))
                .finish()
        }
    }
}