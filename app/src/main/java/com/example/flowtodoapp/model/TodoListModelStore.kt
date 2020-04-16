package com.example.flowtodoapp.model

import com.example.flowtodoapp.base.LiveDataModelStore

class TodoListModelStore : LiveDataModelStore<TodoListModel>(TodoListModel(isLoading = true))