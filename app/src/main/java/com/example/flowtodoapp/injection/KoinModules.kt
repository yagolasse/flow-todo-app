package com.example.flowtodoapp.injection

import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.factory.TodoListIntentFactory
import com.example.flowtodoapp.model.TodoListModel
import com.example.flowtodoapp.model.TodoListModelStore
import com.example.flowtodoapp.repository.ITodoRepository
import com.example.flowtodoapp.repository.TodoRepository
import com.example.flowtodoapp.usecase.ITodoUseCase
import com.example.flowtodoapp.usecase.TodoUseCase
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ITodoUseCase> { TodoUseCase() }
    single<ITodoRepository> { TodoRepository() }
    single { (modelStore: ModelStore<TodoListModel>) -> TodoListIntentFactory(modelStore) }
    viewModel { TodoListModelStore() }
}