package com.example.flowtodoapp.model

import android.text.Editable

sealed class TodoListViewEvent {
    data class Query(val editable: Editable?) : TodoListViewEvent()

    /**
     * This event represents an action requested from model store.
     * Some events are single like open a Dialog or navigation so this tells to the model store that this event is not needed anymore.
     */
    data class CreateTodo(val isAlreadyShown: Boolean = false) : TodoListViewEvent()
}
