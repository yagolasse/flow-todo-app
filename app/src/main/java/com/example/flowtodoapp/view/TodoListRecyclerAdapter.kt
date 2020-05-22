package com.example.flowtodoapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.databinding.ItemTodoBinding
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListEvent
import com.example.flowtodoapp.model.TodoListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TodoListRecyclerAdapter(
    private val scope: CoroutineScope,
    private val intentFactory: ModelStore<TodoListAction, TodoListState, TodoListEvent>
) : RecyclerView.Adapter<TodoItemViewHolder>() {

    var dataSet: List<Todo> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTodoBinding.inflate(inflater, parent, false)
        return TodoItemViewHolder(binding).also {
//            it.viewEvents().onEach { /*event -> intentFactory.process(event)*/ }.launchIn(scope)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
}