package com.example.flowtodoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.FragmentTodoListBinding
import com.example.flowtodoapp.domain.TodoListIntentFactory
import com.example.flowtodoapp.model.TodoListModelStore
import com.example.flowtodoapp.model.TodoListState
import com.example.flowtodoapp.model.TodoListViewEvent
import com.example.flowtodoapp.view.TodoListFragmentDirections.Companion.actionTodoListFragmentToCreateEditTodoFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.afterTextChanges

@FlowPreview
@ExperimentalCoroutinesApi
class TodoListFragment : Fragment(), ViewEventFlow<TodoListViewEvent> {

    private lateinit var binding: FragmentTodoListBinding
    private val modelStore: TodoListModelStore by viewModel()
    private val intentFactory: TodoListIntentFactory by inject {
        parametersOf(modelStore)
    }
    private val adapter: TodoListRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TodoListRecyclerAdapter(viewLifecycleOwner.lifecycleScope, intentFactory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false).apply {
            todoListRecycler.adapter = adapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewEvents().process().launchIn(viewLifecycleOwner.lifecycleScope)
        modelStore.modelState().renderNewState().launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun viewEvents(): Flow<TodoListViewEvent> {
        val flowList = listOf(addButtonFlow(), searchFieldEditTextFlow())
        return flowList.asFlow().flattenMerge(flowList.size)
    }

    private fun addButtonFlow(): Flow<TodoListViewEvent> = binding
        .addButton
        .clicks()
        .map { TodoListViewEvent.CreateEditTodo() }

    private fun searchFieldEditTextFlow(): Flow<TodoListViewEvent> = binding
        .searchFieldEditText
        .afterTextChanges(emitImmediately = true)
        .debounce(timeoutMillis = 500)
        .map { TodoListViewEvent.Query(it.editable) }

    private fun Flow<TodoListViewEvent>.process() = onEach {
        intentFactory.process(it)
    }

    private fun Flow<TodoListState>.renderNewState() = onEach { newState ->
        with(binding) {
            when (newState) {
                is TodoListState.Loading -> renderLoadingState()
                is TodoListState.ErrorWithoutMessage -> renderErrorState()
                is TodoListState.Data -> renderDataState(newState)
                is TodoListState.NavigateToTodoCreateEdit -> navigateToTodoCreateEdit(newState)
                is TodoListState.Empty -> renderEmptyState()
            }
        }
    }

    private fun FragmentTodoListBinding.renderEmptyState() {
        emptyStateLabel.visibility = View.VISIBLE
        todoListRecycler.visibility = View.GONE
        progress.visibility = View.GONE
    }

    private fun navigateToTodoCreateEdit(newState: TodoListState.NavigateToTodoCreateEdit) {
        if (!newState.alreadyExecuted) {
            intentFactory.process(TodoListViewEvent.CreateEditTodo(true, newState.todo))
            newState.takeUnless { it.alreadyExecuted }?.run {
                val action = actionTodoListFragmentToCreateEditTodoFragment(todo)
                findNavController().navigate(action)
            }
        }
    }

    private fun FragmentTodoListBinding.renderDataState(newState: TodoListState.Data) {
        emptyStateLabel.visibility = View.GONE
        todoListRecycler.visibility = View.VISIBLE
        progress.visibility = View.GONE
        adapter.dataSet = newState.dataSet
    }

    private fun FragmentTodoListBinding.renderErrorState() {
        // TODO show error state here
        emptyStateLabel.visibility = View.GONE
        todoListRecycler.visibility = View.GONE
        progress.visibility = View.GONE
    }

    private fun FragmentTodoListBinding.renderLoadingState() {
        emptyStateLabel.visibility = View.GONE
        todoListRecycler.visibility = View.GONE
        progress.visibility = View.VISIBLE
    }
}