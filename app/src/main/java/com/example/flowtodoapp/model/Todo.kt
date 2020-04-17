package com.example.flowtodoapp.model

import java.io.Serializable

data class Todo(val id: Int? = null, val title: String = "", val content: String = "") : Serializable