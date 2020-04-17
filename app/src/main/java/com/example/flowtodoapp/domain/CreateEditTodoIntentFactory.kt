package com.example.flowtodoapp.domain

import com.example.flowtodoapp.base.IntentFactory
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.base.intent
import com.example.flowtodoapp.model.CreateEditTodoModel
import com.example.flowtodoapp.model.CreateEditTodoViewEvent
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent
import org.koin.core.inject

class CreateEditTodoIntentFactory(
    private val modelStore: ModelStore<CreateEditTodoModel>
) : IntentFactory<CreateEditTodoViewEvent>, KoinComponent {

    private val useCase: ITodoUseCase by inject()

    override fun process(viewEvent: CreateEditTodoViewEvent) {
        modelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: CreateEditTodoViewEvent) = when (viewEvent) {
        is CreateEditTodoViewEvent.SaveTodo -> intent<CreateEditTodoModel> {
            if (todo != null) {
                useCase.save(todo).collect {

                }
            }
            copy(isLoading = true)
        }
    }

}