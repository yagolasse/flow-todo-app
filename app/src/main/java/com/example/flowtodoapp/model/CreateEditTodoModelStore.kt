package com.example.flowtodoapp.model

import com.example.flowtodoapp.base.FlowModelStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class CreateEditTodoModelStore(
    todo: Todo?
) : FlowModelStore<CreateEditTodoState>(CreateEditTodoState.DataState.InitialState(todo ?: Todo()))