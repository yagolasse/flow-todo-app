package com.example.flowtodoapp.domain

import com.example.flowtodoapp.base.Middleware
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListState
import com.example.flowtodoapp.repository.ITodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class TodoSearchMiddleware(
    private val repository: ITodoRepository
) : Middleware<TodoListAction, TodoListState> {

    override fun bind(
        actions: Flow<TodoListAction>,
        state: Flow<TodoListState>
    ): Flow<TodoListAction> = actions
        .filterIsInstance<TodoListAction.GetByQuery>()
        .convertInput()
        .flatMapLatest { repository.getTodoListByQuery(it) }
        .map { TodoListAction.DisplayList(it) }

    private fun Flow<TodoListAction.GetByQuery>.convertInput(): Flow<String> = this
        .map { it.query }
        .filterNotNull()
        .filterNot { it.isBlank() }
        .map { it.toString() }
}