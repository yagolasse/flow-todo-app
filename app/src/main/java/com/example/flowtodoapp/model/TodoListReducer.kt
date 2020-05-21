package com.example.flowtodoapp.model

import com.example.flowtodoapp.base.Reducer

class TodoListReducer : Reducer<TodoListState, TodoListAction> {
    override fun reduce(state: TodoListState, action: TodoListAction): TodoListState = when (action) {
        is TodoListAction.GetByQuery -> state
        is TodoListAction.DisplayEmpty -> TodoListState(isEmpty = true)
        is TodoListAction.DisplayLoading -> TodoListState(isLoading = true)
        is TodoListAction.DisplayList -> TodoListState(data = action.data)
        is TodoListAction.DisplayError -> TodoListState(errorMessage = action.throwable.message)
        is TodoListAction.CreateEditTodo -> state.copy(navigateToCreateEditTodo = true)
        is TodoListAction.DismissCreateEditTodo -> state.copy(navigateToCreateEditTodo = false)
    }
}