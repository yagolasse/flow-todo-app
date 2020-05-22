package com.example.flowtodoapp.model

import com.example.flowtodoapp.base.ActionEventConverter

class TodoListActionEventConverter : ActionEventConverter<TodoListAction, TodoListEvent> {
    override fun convert(action: TodoListAction): TodoListEvent? = when (action) {
        is TodoListAction.NavigateToCreateEditTodo -> TodoListEvent.NavigateToCreateEditTodo(action.todo)
        else -> null
    }
}