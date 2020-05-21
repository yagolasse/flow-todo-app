package com.example.flowtodoapp.domain

import android.text.Editable
import com.example.flowtodoapp.base.Middleware
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class TodoSearchMiddleware(
    private val useCase: ITodoUseCase
) : Middleware<TodoListAction, TodoListState> {

    override fun bind(
        actions: Flow<TodoListAction>,
        stateFlow: Flow<TodoListState>
    ): Flow<TodoListAction> = actions
        .filterIsInstance<TodoListAction.GetByQuery>()
        .map { it.query }
        .getTodoListByQuery()
        .catch { TodoListAction.DisplayError(it) }

    private fun Flow<Editable?>.getTodoListByQuery() = flatMapLatest {
        useCase.getTodoListByQuery(it).mapToAction().emitLoadingOnStart()
    }

    private fun Flow<List<Todo>>.mapToAction() = map {
        if (it.isEmpty()) TodoListAction.DisplayEmpty
        else TodoListAction.DisplayList(it)
    }

    private fun Flow<TodoListAction>.emitLoadingOnStart(): Flow<TodoListAction> = onStart {
        emit(TodoListAction.DisplayLoading)
    }
}

