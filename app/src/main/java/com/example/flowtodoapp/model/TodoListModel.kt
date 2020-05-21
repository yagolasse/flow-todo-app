package com.example.flowtodoapp.model

import android.text.Editable

sealed class TodoListAction {
    // Inputs
    data class GetByQuery(val query: Editable?) : TodoListAction()

    // Outputs
    data class DisplayList(val data: List<Todo>) : TodoListAction()
}

data class TodoListState(val data: List<Todo>? = null)