package com.example.flowtodoapp.model

import android.text.Editable

sealed class TodoListAction {
    // Inputs
    data class GetByQuery(val query: Editable?) : TodoListAction()

    // Outputs
    object DisplayEmpty : TodoListAction()
    object DisplayLoading : TodoListAction()
    data class DisplayList(val data: List<Todo>) : TodoListAction()
    data class DisplayError(val throwable: Throwable) : TodoListAction()

    // Events Input
    data class CreateEditTodo(val todo: Todo? = null) : TodoListAction()

    // Events Output
    data class NavigateToCreateEditTodo(val todo: Todo? = null) : TodoListAction()
}

sealed class TodoListEvent {
    data class NavigateToCreateEditTodo(val todo: Todo?) : TodoListEvent()
}

data class TodoListState(
    val isEmpty: Boolean = false,
    val isLoading: Boolean = false,
    val data: List<Todo>? = null,
    val errorMessage: String? = null,
    // Events
    val navigateToCreateEditTodo: Boolean = false
)