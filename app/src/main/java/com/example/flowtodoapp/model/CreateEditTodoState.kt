package com.example.flowtodoapp.model

sealed class CreateEditTodoState {
    sealed class DataState(val todo: Todo) : CreateEditTodoState() {
        class InitialState(todo: Todo) : DataState(todo)
        class Editing(todo: Todo) : DataState(todo)
        class LoadingSave(todo: Todo) : DataState(todo)
    }

    object BackNavigation : CreateEditTodoState()
    object DoneSaving : CreateEditTodoState()
}