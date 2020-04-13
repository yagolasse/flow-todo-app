package com.example.flowtodoapp.injection

import com.example.flowtodoapp.repository.ITodoRepository
import com.example.flowtodoapp.repository.TodoRepository
import com.example.flowtodoapp.usecase.ITodoUseCase
import com.example.flowtodoapp.usecase.TodoUseCase
import com.example.flowtodoapp.view.ITodoListView
import com.example.flowtodoapp.viewmodel.ITodoListViewModel
import com.example.flowtodoapp.viewmodel.TodoListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<ITodoListViewModel> { TodoListViewModel() }
    single<ITodoUseCase> { TodoUseCase() }
    single<ITodoRepository> { TodoRepository() }
}