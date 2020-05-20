package com.example.flowtodoapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowtodoapp.domain.TodoUseCase
import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

interface Reducer<S, A> {
    fun reduce(state: S, action: A): S
}

interface Middleware<A, S> {
    fun bind(actions: Flow<A>, state: Flow<S>): Flow<A>
}

@FlowPreview
@ExperimentalCoroutinesApi
class Store<A, S>(
    reducer: Reducer<S, A>,
    middlewareList: List<Middleware<A, S>>,
    initialState: S
) : ViewModel() {

    private val actions = Channel<A>()
    private val store = ConflatedBroadcastChannel(initialState)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                store.offer(reducer.reduce(store.value, actions.receive()))
            }
        }

        middlewareList.map {
            it.bind(actions.consumeAsFlow(), store.asFlow())
        }.asFlow().flattenMerge(middlewareList.size).onEach {
            actions.offer(it)
        }.launchIn(viewModelScope)
    }

    fun process(action: A) {
        actions.offer(action)
    }

    fun storeState(): Flow<S> = store.asFlow().distinctUntilChanged()
}

class TodoListReducer : Reducer<State, Action> {

    override fun reduce(state: State, action: Action): State = when (action) {
        is Action.SearchLoading -> State.Loading
        is Action.SearchFailure -> State.Error
        is Action.Search -> state
        is Action.SearchSuccess -> State.Success
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
class SearchMiddleware(private val useCase: TodoUseCase) : Middleware<Action, State> {
    override fun bind(actions: Flow<Action>, state: Flow<State>): Flow<Action> = actions
        .filterIsInstance<Action.Search>()
        .combine(state) { action, currentState -> action to currentState }
        .filterNot { (_, currentState) -> currentState is State.Loading }
        .onStart { State.Loading }
        .flatMapLatest { (action) -> useCase.getTodoListByQuery(action.query) }
        .map { Action.SearchSuccess(it) }
        .catch { Action.SearchFailure }

}

@FlowPreview
@ExperimentalCoroutinesApi
class DialogMiddleware : Middleware<Action, State> {
    override fun bind(actions: Flow<Action>, state: Flow<State>): Flow<Action> = actions
        .filterIsInstance<Action.SearchLoading>()
        .onEach { State.Dialog("Blá") }
}

sealed class Action {
    object SearchLoading : Action()
    object SearchFailure : Action()
    data class Search(val query: String) : Action()
    data class SearchSuccess(val data: List<Todo>) : Action()
}

sealed class State {
    object Success : State()
    data class Dialog(val text: String) : State()
    object Loading : State()
    object Error : State()
}