package com.example.mytask.ui.addTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytask.database.TaskDatabaseDao

class AddTaskViewModelFactory(
    private val taskKey: Long,
    private val dataSource: TaskDatabaseDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTaskViewModel::class.java)) {
            return AddTaskViewModel(taskKey, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}