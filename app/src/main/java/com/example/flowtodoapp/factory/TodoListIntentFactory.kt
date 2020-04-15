package com.example.flowtodoapp.factory

import com.example.flowtodoapp.base.IntentFactory
import com.example.flowtodoapp.base.intent
import com.example.flowtodoapp.model.TodoListModel
import com.example.flowtodoapp.model.TodoListModelStore
import com.example.flowtodoapp.model.TodoListViewEvent
import com.example.flowtodoapp.usecase.ITodoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class TodoListIntentFactory : IntentFactory<TodoListViewEvent>, KoinComponent {

    private val scope = MainScope()
    private val useCase: ITodoUseCase by inject()
    private val modelStore: TodoListModelStore by inject()

    override fun process(viewEvent: TodoListViewEvent) {
        modelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewViewEvent: TodoListViewEvent) = intent<TodoListModel> {
        scope.launch(Dispatchers.IO) {
            useCase.getTodoListByQuery(viewViewEvent.editable)
                .catch { exception ->
                    copy(dataSet = null, isLoading = false, errorMessage = exception.message)
                }
                .collect {
                    modelStore.process(intent { copy(dataSet = it, isLoading = false, errorMessage = null) })
                }
        }
        copy(dataSet = null, isLoading = true, errorMessage = null)
    }
}