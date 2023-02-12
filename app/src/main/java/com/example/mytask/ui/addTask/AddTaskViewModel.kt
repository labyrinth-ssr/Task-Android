package com.example.mytask.ui.addTask

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.dateStr2timeStamp
import kotlinx.coroutines.launch

class AddTaskViewModel(
    private val taskKey: Long = 0L,
    val database: TaskDatabaseDao) : ViewModel() {

    private val task : LiveData<Task>
    private val dateString = MutableLiveData<String>()
    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome:LiveData<Boolean?>
    get() = _navigateToHome

    init {
        task = database.getTaskWithId(taskKey)
    }

    fun onSetPriority(priority:Int) {
        task.value!!.priority = priority
    }

    fun doneNavigating(){
        _navigateToHome.value = null
    }

    fun getTask(): Task? {
        return task.value;
    }

    fun setTaskName(taskName:String){
        task.value!!.taskName = taskName
    }

    fun setStartTime(dateStr:String){
        task.value!!.startTimeStamp = dateStr2timeStamp(dateStr)
    }

    fun onAddTask(){
        viewModelScope.launch {
            Log.i(TAG, "onAddTask: $task")
            task.value?.let { insert(it) }
//            _navigateToHome.value = true
        }
    }

    fun setDate(date:String){
        dateString.value = date
    }

    private suspend fun insert(newTask: Task) {
        database.insert(newTask)
    }
}