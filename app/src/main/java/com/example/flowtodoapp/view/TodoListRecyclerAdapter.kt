package com.example.flowtodoapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.ItemTodoBinding
import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.flow.*

class TodoListRecyclerAdapter : RecyclerView.Adapter<TodoItemViewHolder>(), ViewEventFlow<Todo> {

    var dataSet: List<Todo> = listOf()
        set(value) {
            field = value
            dataSetEvents.clear()
            notifyDataSetChanged()
        }

    private val dataSetEvents: MutableList<Flow<Todo>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTodoBinding.inflate(inflater, parent, false)
        return TodoItemViewHolder(binding)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
//        val clickEventFlow = holder.bind(dataSet[position]).collect {
//
//        }
//        dataSetEvents.add(clickEventFlow)
    }

    override fun viewEvents(): Flow<Todo> = dataSetEvents.asFlow().flattenConcat()
}