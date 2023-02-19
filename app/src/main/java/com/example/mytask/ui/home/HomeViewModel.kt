package com.example.mytask.ui.home

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.formatTasks
import com.example.mytask.service.TaskCompleter
import kotlinx.coroutines.launch

class HomeViewModel (
    val database: TaskDatabaseDao,
    application: Application
        ):AndroidViewModel(application){
    var tasks = database.getAllTasks()
    private var todayTask = MutableLiveData<Task?>()
    private val _navigateToAddTask = MutableLiveData<Task?>()
    lateinit var taskCompleter: TaskCompleter

    val navigateToAddTask: MutableLiveData<Task?>
        get() = _navigateToAddTask

    fun doneNavigating() {
        _navigateToAddTask.value = null
    }

    fun setComplete(task1: Task,completed:Boolean){
        viewModelScope.launch {
            taskCompleter.setComplete(task1, completed)
        }
    }

    init {
        initializeTodayTask()
        tasks = database.getAllTasks()
    }

    private fun initializeTodayTask() {
        viewModelScope.launch {
            todayTask.value = getTodayTaskFromDatabase()
        }
    }

    private suspend fun getTodayTaskFromDatabase(): Task? {
        var task = database.getDueTodayTask()
        return task;
    }

    fun navigateToAddTask(){
        val newTask = Task()
        _navigateToAddTask.value = newTask
    }

    fun onTaskClicked(task: Task){
        _navigateToAddTask.value = task
    }

//    fun taskCompleted(task: Task){
//        database.
//    }

}