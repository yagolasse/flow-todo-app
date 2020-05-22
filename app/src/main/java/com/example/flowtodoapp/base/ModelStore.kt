package com.example.flowtodoapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class ModelStore<A, S, E>(
    private val reducer: Reducer<S, A, E>,
    middlewareList: List<Middleware<A, S>>,
    initialState: S
) : ViewModel(), Store<A, S, E> {

    private val actions = BroadcastChannel<A>(DEFAULT_BUFFER_SIZE)
    private val events = BroadcastChannel<E>(DEFAULT_BUFFER_SIZE)
    private val store = MutableStateFlow(initialState)

    init {
        val actionFlow = actions
            .asFlow()
            .distinctUntilChanged()
            .applyReducer()
            .flowOn(Dispatchers.Main)

        middlewareList
            .bindReturningAction(actionFlow)
            .asFlow()
            .flattenMerge(middlewareList.size)
            .feedbackToActions()
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)

        actionFlow.launchIn(viewModelScope)
    }

    override fun process(action: A) {
        actions.offer(action)
    }

    override fun storeState(): Flow<S> = store

    override fun storeEvents(): Flow<E> = events.asFlow()

    private fun Flow<A>.applyReducer() = onEach {
        val (state, event) = reducer.reduce(store.value, it)
        store.value = state
        if (event != null) {
            events.offer(event)
        }
    }

    private fun List<Middleware<A, S>>.bindReturningAction(actionFlow: Flow<A>) = map {
        it.bind(actionFlow, store)
    }

    private fun Flow<A>.feedbackToActions() = onEach {
        actions.offer(it)
    }
}
