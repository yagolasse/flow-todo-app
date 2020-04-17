package com.example.flowtodoapp.model

sealed class TodoListState {
    object Loading : TodoListState()
    object ErrorWithoutMessage : TodoListState()
    object Empty : TodoListState()
    data class Data(val dataSet: List<Todo>) : TodoListState()
    data class Error(val errorMessage: String) : TodoListState()
    data class NavigateToTodoCreateEdit(val todo: Todo? = null, override val alreadyExecuted: Boolean = false) : TodoListState(), SingleEvent
}

interface SingleEvent {
    val alreadyExecuted: Boolean
}