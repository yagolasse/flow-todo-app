package com.example.flowtodoapp.scenes.counter

import com.example.flowtodoapp.base.Middleware
import com.example.flowtodoapp.scenes.model.CounterAction
import com.example.flowtodoapp.scenes.model.CounterState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance

class IncrementCounterMiddleware : Middleware<CounterAction, CounterState> {

    override fun bind(actions: Flow<CounterAction>, state: Flow<CounterState>): Flow<CounterAction> = actions
        .filterIsInstance<CounterAction.Increment>()
        .combine(state) { action, currentState ->
            val newValue = currentState.value + action.incrementAmount
            CounterAction.ShowValue(newValue)
        }
}