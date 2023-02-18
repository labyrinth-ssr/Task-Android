package com.example.mytask.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@HiltViewModel
class CalendarPickerViewModel @Inject constructor(
    private val calendarProvider: CalendarProvider
) : ViewModel() {

    data class ViewState(
        val calendars: List<AndroidCalendar> = emptyList(),
    )

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState>
        get() = _viewState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.M)
    fun loadCalendars() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(calendars = calendarProvider.calendars) }
        }
    }

    init {
        loadCalendars()
    }
}