package com.example.flowtodoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.FragmentTodoListBinding
import com.example.flowtodoapp.domain.TodoListIntentFactory
import com.example.flowtodoapp.model.TodoListModelStore
import com.example.flowtodoapp.model.TodoListState
import com.example.flowtodoapp.model.TodoListViewEvent
import com.example.flowtodoapp.view.TodoListFragmentDirections.Companion.actionTodoListFragmentToCreateEditTodoFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.afterTextChanges

class TodoListFragment : Fragment(), ViewEventFlow<TodoListViewEvent> {

    private val adapter = TodoListRecyclerAdapter()
    private val modelStore: TodoListModelStore by viewModel()
    private val intentFactory: TodoListIntentFactory by inject { parametersOf(modelStore) }
    private lateinit var binding: FragmentTodoListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false).apply {
            todoListRecycler.adapter = adapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewEvents().process().launchIn(viewLifecycleOwner.lifecycleScope)
        modelStore.modelState().renderNewState()
    }

    override fun viewEvents(): Flow<TodoListViewEvent> {
        val flowList: List<Flow<TodoListViewEvent>> = listOf(
            adapter.viewEvents().map { clickedItem ->
                TodoListViewEvent.CreateEditTodo(editItem = clickedItem)
            },
            binding.addButton.clicks().map {
                TodoListViewEvent.CreateEditTodo()
            },
            binding.searchFieldEditText.afterTextChanges(emitImmediately = true).debounce(timeoutMillis = 500).map {
                TodoListViewEvent.Query(it.editable)
            }
        )

        return flowList.asFlow().flattenMerge(flowList.size)
    }

    private fun Flow<TodoListViewEvent>.process(): Flow<TodoListViewEvent> = onEach {
        intentFactory.process(it)
    }

    private fun LiveData<TodoListState>.renderNewState() {
        observe(viewLifecycleOwner, Observer { newState ->
            when (newState) {
                is TodoListState.Loading -> renderLoadingState()
                is TodoListState.ErrorWithoutMessage -> renderErrorState()
                is TodoListState.Data -> renderDataState(newState)
                is TodoListState.Error -> {
                    /* Does Nothing for now */
                }
                is TodoListState.NavigateToTodoCreateEdit -> navigateToTodoCreateEdit(newState)
                is TodoListState.Empty -> renderEmptyState()
            }
        })
    }

    private fun renderEmptyState() {
        with(binding) {
            emptyStateLabel.visibility = View.VISIBLE
            todoListRecycler.visibility = View.GONE
            progress.visibility = View.GONE
        }
    }

    private fun navigateToTodoCreateEdit(newState: TodoListState.NavigateToTodoCreateEdit) {
        newState.takeUnless { it.alreadyExecuted }?.run {
            val action = actionTodoListFragmentToCreateEditTodoFragment(todo)
            findNavController().navigate(action)
        }
    }

    private fun renderDataState(newState: TodoListState.Data) {
        with(binding) {
            emptyStateLabel.visibility = View.GONE
            todoListRecycler.visibility = View.VISIBLE
            progress.visibility = View.GONE
            adapter.dataSet = newState.dataSet
        }
    }

    private fun renderErrorState() {
        with(binding) {
            // TODO show error state here
            emptyStateLabel.visibility = View.GONE
            todoListRecycler.visibility = View.GONE
            progress.visibility = View.GONE
        }
    }

    private fun renderLoadingState() {
        with(binding) {
            emptyStateLabel.visibility = View.GONE
            todoListRecycler.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }
    }
}