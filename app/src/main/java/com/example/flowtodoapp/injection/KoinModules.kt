package com.example.flowtodoapp.injection

import com.example.flowtodoapp.R
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.domain.ITodoUseCase
import com.example.flowtodoapp.domain.TodoNavigationMiddleware
import com.example.flowtodoapp.domain.TodoSearchMiddleware
import com.example.flowtodoapp.domain.TodoUseCase
import com.example.flowtodoapp.model.*
import com.example.flowtodoapp.repository.ITodoRepository
import com.example.flowtodoapp.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
val appModule = module {
    single<ITodoUseCase> {
        val emptyTodoTitleErrorMessage = androidContext().getString(R.string.empty_title_error_message)
        TodoUseCase(emptyTodoTitleErrorMessage)
    }
    single<ITodoRepository> { TodoRepository() }
    viewModel {
        ModelStore(
            TodoListReducer(),
            TodoListActionEventConverter(),
            listOf(
                TodoSearchMiddleware(get()),
                TodoNavigationMiddleware()
            ),
            TodoListState()
        )
    }
}