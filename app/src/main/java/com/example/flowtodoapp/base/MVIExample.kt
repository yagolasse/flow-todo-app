package com.example.flowtodoapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowtodoapp.domain.TodoUseCase
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.repository.TodoRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.lang.IllegalArgumentException

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
    private val store = MutableStateFlow(initialState)

    init {
        val actionFlow = actions
            .receiveAsFlow()
            .onEach { store.value = reducer.reduce(store.value, it) }
            .flowOn(Dispatchers.Main)

        actionFlow.launchIn(viewModelScope)

        middlewareList
            .map { it.bind(actionFlow, store) }
            .asFlow()
            .flattenMerge(middlewareList.size)
            .onEach { actions.offer(it) }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    fun process(action: A) {
        actions.offer(action)
    }

    fun storeState(): Flow<S> = store
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
class SearchMiddleware(private val useCase: TodoRepository) : Middleware<Action, State> {


    override fun bind(actions: Flow<Action>, state: Flow<State>): Flow<Action> = actions
        .filterIsInstance<Action.Search>()
        .combine(state) { action, currentState -> action to currentState }
        .filterNot { (_, currentState) -> currentState is State.Loading }
        .onStart { State.Loading }
        .onEach { (action) ->
            if (action.query.isBlank()) {
                throw IllegalArgumentException()
            }
        }
        .flowOn(Dispatchers.Main)
        .flatMapLatest { (action) -> useCase.getTodoListByQuery(action.query) }
        .flowOn(Dispatchers.IO)
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