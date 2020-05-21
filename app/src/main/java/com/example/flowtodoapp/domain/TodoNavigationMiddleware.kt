package com.example.flowtodoapp.domain

import com.example.flowtodoapp.base.Middleware
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class TodoNavigationMiddleware : Middleware<TodoListAction, TodoListState> {

    override fun bind(actions: Flow<TodoListAction>, stateFlow: Flow<TodoListState>): Flow<TodoListAction> = stateFlow
        .filter { it.navigateToCreateEditTodo }
        .map { TodoListAction.DismissCreateEditTodo }
}