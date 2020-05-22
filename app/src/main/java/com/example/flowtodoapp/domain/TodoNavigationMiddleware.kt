package com.example.flowtodoapp.domain

import com.example.flowtodoapp.base.Middleware
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

@FlowPreview
@ExperimentalCoroutinesApi
class TodoNavigationMiddleware : Middleware<TodoListAction, TodoListState> {

    override fun bind(actions: Flow<TodoListAction>, stateFlow: Flow<TodoListState>): Flow<TodoListAction> = actions
        .filterIsInstance<TodoListAction.CreateEditTodo>()
        .map { it.todo }
        .map { TodoListAction.NavigateToCreateEditTodo(it) }
}