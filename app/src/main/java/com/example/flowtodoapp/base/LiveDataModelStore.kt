package com.example.flowtodoapp.base

import androidx.lifecycle.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

open class LiveDataModelStore<S>(initialState: S) : ModelStore<S>, ViewModel() {

    private val scope = MainScope()
    private val intents = Channel<Intent<S>>()
    private val store = MutableLiveData(initialState)

    init {
        viewModelScope.launch {
            while (isActive) {
                store.value?.run oldState@{ store.postValue(intents.receive().reduce(this@oldState)) }
            }
        }
    }

    override fun process(intent: Intent<S>) {
        intents.offer(intent)
    }

    override fun modelState(): LiveData<S> = store.distinctUntilChanged()

    override fun onCleared() {
        intents.close()
        super.onCleared()
    }
}