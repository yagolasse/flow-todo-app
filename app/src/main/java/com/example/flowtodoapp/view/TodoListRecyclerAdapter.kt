package com.example.flowtodoapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.flowtodoapp.base.IntentFactory
import com.example.flowtodoapp.databinding.ItemTodoBinding
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.model.TodoListViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
class TodoListRecyclerAdapter(
    private val scope: CoroutineScope,
    private val intentFactory: IntentFactory<TodoListViewEvent>
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
            it.viewEvents().onEach { event -> intentFactory.process(event) }.launchIn(scope)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
}