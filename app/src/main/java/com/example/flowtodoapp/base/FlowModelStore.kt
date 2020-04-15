package com.example.flowtodoapp.base

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

open class FlowModelStore<S>(initialState: S) : ModelStore<S> {

    private val scope = MainScope()
    private val intents = Channel<Intent<S>>()
    private val store = ConflatedBroadcastChannel(initialState)

    init {
        scope.launch {
            while (isActive) {
                store.offer(intents.receive().reduce(store.value))
            }
        }
    }

    override fun process(intent: Intent<S>) {
        intents.offer(intent)
    }

    override fun modelState(): Flow<S> = store.asFlow().distinctUntilChanged()

    fun close() {
        intents.close()
        store.close()
        scope.cancel()
    }
}