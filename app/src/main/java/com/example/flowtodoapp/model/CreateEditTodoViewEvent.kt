package com.example.flowtodoapp.model

import android.text.Editable

sealed class CreateEditTodoViewEvent {
    object SaveTodo : CreateEditTodoViewEvent()
    object BackNavigation : CreateEditTodoViewEvent()
    data class EditTodo(val title: Editable?, val content: Editable?) : CreateEditTodoViewEvent()
}