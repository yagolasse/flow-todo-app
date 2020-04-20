package com.example.flowtodoapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.FragmentCreateEditTodoBinding
import com.example.flowtodoapp.domain.CreateEditTodoIntentFactory
import com.example.flowtodoapp.model.CreateEditTodoModelStore
import com.example.flowtodoapp.model.CreateEditTodoState
import com.example.flowtodoapp.model.CreateEditTodoViewEvent
import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.activity.backPresses
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.afterTextChanges

@FlowPreview
@ExperimentalCoroutinesApi
class CreateEditTodoFragment : Fragment(), ViewEventFlow<CreateEditTodoViewEvent> {

    private lateinit var binding: FragmentCreateEditTodoBinding
    private val args: CreateEditTodoFragmentArgs by navArgs()
    private val modelStore: CreateEditTodoModelStore by viewModel {
        parametersOf(args.todo)
    }
    private val intentFactory: CreateEditTodoIntentFactory by inject {
        parametersOf(modelStore)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCreateEditTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Using this callback because of the back pressed event
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        modelStore.modelState().renderNewState().launchIn(viewLifecycleOwner.lifecycleScope)
        viewEvents().process().launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun viewEvents(): Flow<CreateEditTodoViewEvent> {
        val flowList: MutableList<Flow<CreateEditTodoViewEvent>> = mutableListOf(saveButtonClickFlow(), editingFlow())
        backPressesFlow()?.run { flowList.add(this) }

        return flowList.asFlow().flattenMerge(flowList.size)
    }

    private fun Flow<CreateEditTodoViewEvent>.process() = onEach { event ->
        intentFactory.process(event)
    }

    private fun saveButtonClickFlow() = binding.finishEditingButton.clicks().map {
        CreateEditTodoViewEvent.SaveTodo
    }

    private fun backPressesFlow() = activity?.onBackPressedDispatcher?.backPresses(viewLifecycleOwner)?.map {
        CreateEditTodoViewEvent.BackNavigation
    }

    private fun editingFlow(): Flow<CreateEditTodoViewEvent> {
        val titleTextFlow = binding.todoTitleEditText.afterTextChanges()
        val contentTextFlow = binding.todoContentEditText.afterTextChanges()
        return titleTextFlow.combine(contentTextFlow) { title, content ->
            CreateEditTodoViewEvent.EditTodo(title.editable, content.editable)
        }
    }

    private fun Flow<CreateEditTodoState>.renderNewState() = onEach { newState ->
        when (newState) {
            /* Do nothing for editing state*/
            is CreateEditTodoState.DataState.InitialState -> renderInitialState(newState.todo)
            is CreateEditTodoState.DataState.LoadingSave -> renderLoadingSave(newState.todo)
            is CreateEditTodoState.DoneSaving -> renderDoneSaving()
            is CreateEditTodoState.BackNavigation -> renderBackNavigation()
            // TODO create states for error saving
        }
    }

    private fun renderInitialState(todo: Todo) {
        with(binding) {
            todoTitleEditText.setText(todo.title)
            todoContentEditText.setText(todo.content)
        }
    }

    private fun renderLoadingSave(todo: Todo) {

    }

    private fun renderDoneSaving() {
        findNavController().popBackStack()
    }

    private fun renderBackNavigation() {
        findNavController().popBackStack()
    }
}
