package com.example.flowtodoapp.model

import android.text.Editable

sealed class TodoListViewEvent {
    data class Query(val editable: Editable?) : TodoListViewEvent()
    data class CreateEditTodo(val alreadyExecuted: Boolean = false, val editItem: Todo? = null) : TodoListViewEvent()
}
