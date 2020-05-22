package com.example.flowtodoapp.model

import com.example.flowtodoapp.base.Reducer

class TodoListReducer : Reducer<TodoListState, TodoListAction, TodoListEvent> {
    override fun reduce(state: TodoListState, action: TodoListAction): Pair<TodoListState, TodoListEvent?> = when (action) {
        // Actions
        // Inputs
        is TodoListAction.GetByQuery -> state to null
        is TodoListAction.CreateEditTodo -> state to null
        // Outputs
        is TodoListAction.DisplayEmpty -> TodoListState(isEmpty = true) to null
        is TodoListAction.DisplayLoading -> TodoListState(isLoading = true) to null
        is TodoListAction.DisplayList -> TodoListState(data = action.data) to null
        is TodoListAction.DisplayError -> TodoListState(errorMessage = action.throwable.message) to null
        // Events
        is TodoListAction.NavigateToCreateEditTodo -> state to TodoListEvent.NavigateToCreateEditTodo(action.todo)
    }


}