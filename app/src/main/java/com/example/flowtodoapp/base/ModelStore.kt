package com.example.flowtodoapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class ModelStore<A, S>(
    private val reducer: Reducer<S, A>,
    middlewareList: List<Middleware<A, S>>,
    initialState: S
) : ViewModel(), Store<A, S> {

    private val actions = Channel<A>()
    private val store = MutableStateFlow(initialState)

    init {
        val actionFlow = actions
            .receiveAsFlow()
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

    private fun Flow<A>.applyReducer() = onEach {
        store.value = reducer.reduce(store.value, it)
    }

    private fun List<Middleware<A, S>>.bindReturningAction(actionFlow: Flow<A>) = map {
        it.bind(actionFlow, store)
    }

    private fun Flow<A>.feedbackToActions() = onEach {
        actions.offer(it)
    }
}
