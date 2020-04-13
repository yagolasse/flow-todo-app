package com.example.flowtodoapp.model

sealed class State
object LoadingState : State()
class ErrorState(val cause: Throwable) : State()

// Todo List View State
class TodoListDataState(val dataSet: List<Todo>) : State()
object TodoListEmptyState : State()