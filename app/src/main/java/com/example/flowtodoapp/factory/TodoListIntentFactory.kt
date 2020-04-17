package com.example.flowtodoapp.factory

import com.example.flowtodoapp.base.IntentFactory
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.base.intent
import com.example.flowtodoapp.model.TodoListModel
import com.example.flowtodoapp.model.TodoListViewEvent
import com.example.flowtodoapp.usecase.ITodoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class TodoListIntentFactory(
    private val modelStore: ModelStore<TodoListModel>
) : IntentFactory<TodoListViewEvent>, KoinComponent {

    private val scope = MainScope()
    private val useCase: ITodoUseCase by inject()

    override fun process(viewEvent: TodoListViewEvent) {
        modelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: TodoListViewEvent) = when (viewEvent) {
        is TodoListViewEvent.Query -> intent<TodoListModel> {
            scope.launch(Dispatchers.IO) {
                useCase.getTodoListByQuery(viewEvent.editable)
                    .catch { exception ->
                        modelStore.process(intent {
                            copy(
                                dataSet = null,
                                isLoading = false,
                                errorMessage = exception.message,
                                shouldOpenTodoCreation = false
                            )
                        })
                    }
                    .collect {
                        modelStore.process(intent {
                            copy(
                                dataSet = it,
                                isLoading = false,
                                errorMessage = null,
                                shouldOpenTodoCreation = false
                            )
                        })
                    }
            }
            copy(dataSet = null, isLoading = true, errorMessage = null, shouldOpenTodoCreation = false)
        }
        is TodoListViewEvent.CreateEditTodo -> intent {
            copy(shouldOpenTodoCreation = !viewEvent.isAlreadyShown)
        }
    }
}