package com.example.flowtodoapp.model

import com.example.flowtodoapp.base.FlowModelStore

class TodoListModelStore : FlowModelStore<TodoListModel>(TodoListModel(isLoading = true))