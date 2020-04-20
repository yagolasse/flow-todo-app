package com.example.flowtodoapp.base

import androidx.lifecycle.*
import com.example.flowtodoapp.model.TodoListState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@FlowPreview
@ExperimentalCoroutinesApi
open class FlowModelStore<S>(initialState: S) : ModelStore<S>, ViewModel() {

    private val scope = MainScope()
    private val intents = Channel<Intent<S>>()
    private val store = ConflatedBroadcastChannel(initialState)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                store.offer(intents.receive().reduce(store.value))
            }
        }
    }

    override fun process(intent: Intent<S>) {
        intents.offer(intent)
    }

    override fun modelState(): Flow<S> = store.asFlow().distinctUntilChanged()

    override fun onCleared() {
        intents.close()
        super.onCleared()
    }
}