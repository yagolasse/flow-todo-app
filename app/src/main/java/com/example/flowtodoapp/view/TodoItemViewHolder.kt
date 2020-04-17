package com.example.flowtodoapp.view

import androidx.recyclerview.widget.RecyclerView
import com.example.flowtodoapp.databinding.ItemTodoBinding
import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import reactivecircus.flowbinding.android.view.clicks

class TodoItemViewHolder(
    private val binding: ItemTodoBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Todo): Flow<Todo> {
        with(binding) {
            todoTitle.text = item.title
            todoContent.text = item.content
        }
        return binding.itemTodoContainer.clicks().map { item }
    }
}