package com.example.flowtodoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.FragmentTodoListBinding
import com.example.flowtodoapp.domain.TodoListIntentFactory
import com.example.flowtodoapp.model.TodoListModel
import com.example.flowtodoapp.model.TodoListModelStore
import com.example.flowtodoapp.model.TodoListViewEvent
import kotlinx.android.synthetic.main.fragment_todo_list.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.afterTextChanges

class TodoListFragment : Fragment(), ViewEventFlow<TodoListViewEvent> {

    private val adapter = TodoListRecyclerAdapter()
    private val dismissEventChannel = Channel<TodoListViewEvent>()
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
        modelStore.modelState().forTodoList()
    }

    override fun viewEvents(): Flow<TodoListViewEvent> {
        val flowList: List<Flow<TodoListViewEvent>> = listOf(
            dismissEventChannel.receiveAsFlow(),
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

    private fun LiveData<TodoListModel>.forTodoList() {
        observe(viewLifecycleOwner, Observer { (data, loading, error, shouldOpenCreateTodo) ->
            if (shouldOpenCreateTodo) {
                // TODO open created todo screen
                Toast.makeText(context, "Worked", Toast.LENGTH_SHORT).show()
                dismissEventChannel.offer(TodoListViewEvent.CreateEditTodo(isAlreadyShown = true))
            } else {
                progress.visibility = if (loading) {
                    emptyStateLabel.visibility = View.GONE
                    todoListRecycler.visibility = View.GONE
                    View.VISIBLE
                } else {
                    if (data.isNullOrEmpty()) {
                        emptyStateLabel.visibility = View.VISIBLE
                        todoListRecycler.visibility = View.GONE
                    }
                    if (data?.isNotEmpty() == true) {
                        emptyStateLabel.visibility = View.GONE
                        todoListRecycler.visibility = View.VISIBLE
                        adapter.dataSet = data
                    }
                    View.GONE
                }
                if (error != null) {
                    TODO("Missing show error")
                }
            }
        })
    }
}