package com.example.flowtodoapp.injection

import android.text.Editable
import com.example.flowtodoapp.factory.TodoListIntentFactory
import com.example.flowtodoapp.model.TodoListModelStore
import com.example.flowtodoapp.model.TodoListViewEvent
import com.example.flowtodoapp.repository.ITodoRepository
import com.example.flowtodoapp.repository.TodoRepository
import com.example.flowtodoapp.usecase.ITodoUseCase
import com.example.flowtodoapp.usecase.TodoUseCase
import org.koin.dsl.module

val appModule = module {
    single<ITodoUseCase> { TodoUseCase() }
    single<ITodoRepository> { TodoRepository() }
    single { TodoListIntentFactory() }
    single { TodoListModelStore() }
    factory { (editable: Editable?) ->
        TodoListViewEvent(editable)
    }
}