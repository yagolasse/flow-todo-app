package com.example.flowtodoapp.model

import com.example.flowtodoapp.base.LiveDataModelStore

class CreateEditTodoModelStore(
    todo: Todo?
) : LiveDataModelStore<CreateEditTodoModel>(CreateEditTodoModel(todo = todo))