package com.example.flowtodoapp.model

import android.text.Editable

sealed class CreateEditTodoViewEvent {
    data class SaveTodo(val title: Editable?, val content: Editable?) : CreateEditTodoViewEvent()
}