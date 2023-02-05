package com.example.mytask.ui.home

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.formatTasks
import kotlinx.coroutines.launch

class HomeViewModel (
    val database: TaskDatabaseDao,
    application: Application
        ):AndroidViewModel(application){
    val tasks = database.getAllTasks()
    val tasksString = Transformations.map(tasks) {tasks->
        formatTasks(tasks,application.resources)
    }

    private var todayTask = MutableLiveData<Task?>()
    private val _navigateToAddTask = MutableLiveData<Task?>()

    val navigateToAddTask: MutableLiveData<Task?>
        get() = _navigateToAddTask

    fun doneNavigating() {
        _navigateToAddTask.value = null
    }

    init {
        initializeTodayTask()
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
    fun onAddTask(){
//        Log.i(TAG, "onAddTask: tasks:"+ (tasks.value?.get(0)?.taskName ?: ""))
        viewModelScope.launch {
            val newTask = Task()
            insert(newTask)
            todayTask.value = getTodayTaskFromDatabase()

            _navigateToAddTask.value = newTask

        }
        Log.i(TAG, "onAddTask:"+ tasksString.value)

    }

    private suspend fun insert(newTask: Task) {
        database.insert(newTask)
    }

}