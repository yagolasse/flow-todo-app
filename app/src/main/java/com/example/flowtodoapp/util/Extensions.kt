package com.example.flowtodoapp.util

import android.view.View

// View extensions
infix fun View.isVisibleIf(shouldShow: Boolean) {
    visibility = if (shouldShow) View.VISIBLE else View.GONE
}