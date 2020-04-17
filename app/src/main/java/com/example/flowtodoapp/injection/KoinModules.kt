package com.example.flowtodoapp.injection

import com.example.flowtodoapp.R
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.domain.CreateEditTodoIntentFactory
import com.example.flowtodoapp.domain.TodoListIntentFactory
import com.example.flowtodoapp.repository.ITodoRepository
import com.example.flowtodoapp.repository.TodoRepository
import com.example.flowtodoapp.domain.ITodoUseCase
import com.example.flowtodoapp.domain.TodoUseCase
import com.example.flowtodoapp.model.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ITodoUseCase> {
        val emptyTodoTitleErrorMessage = androidContext().getString(R.string.empty_title_error_message)
        TodoUseCase(emptyTodoTitleErrorMessage)
    }
    single<ITodoRepository> { TodoRepository() }
    // intent factory
    single { (stateStore: ModelStore<TodoListState>) ->
        TodoListIntentFactory(stateStore)
    }
    single { (modelStore: ModelStore<CreateEditTodoModel>) ->
        CreateEditTodoIntentFactory(modelStore)
    }
    // model store
    viewModel { TodoListModelStore() }
    viewModel { (todo: Todo?) ->
        CreateEditTodoModelStore(todo)
    }
}