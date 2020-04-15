package com.example.flowtodoapp.model

data class TodoListModel(
    val dataSet: List<Todo>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)