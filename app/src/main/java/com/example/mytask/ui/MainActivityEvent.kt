package com.example.mytask.ui

import com.example.mytask.database.Task
import kotlinx.coroutines.flow.MutableSharedFlow

typealias MainActivityEventBus = MutableSharedFlow<MainActivityEvent>

sealed interface MainActivityEvent {
    data class OpenTask(val task: Task) : MainActivityEvent
    object RequestRating : MainActivityEvent
    object ClearTaskEditFragment : MainActivityEvent
}