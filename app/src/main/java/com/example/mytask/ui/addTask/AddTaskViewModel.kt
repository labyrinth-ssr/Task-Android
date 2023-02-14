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

    val task = MutableLiveData<Task>()
    val subtasks: LiveData<List<Task>> = database.getChildren(taskKey)
//    val taskContainer =
    init {
        if (taskKey != 0L){
            viewModelScope.launch {
                task.value = database.get(taskKey)
                Log.i(TAG, "task init: "+ (task.value?.taskName)+"task id:"+taskKey)
            }
        }
        else task.value = Task()
    }
    private val dateString = MutableLiveData<String>()
    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome:LiveData<Boolean?>
    get() = _navigateToHome

    fun onSetPriority(priority:Int) {
        task.value!!.priority = priority
    }

    fun doneNavigating(){
        _navigateToHome.value = null
    }
//
//    fun getTask(): Task? {
//        return task.value;
//    }

    fun setTaskName(taskName:String){
        task.value?.taskName = taskName
    }

    fun setStartTime(dateStr:String){
        task.value?.startTimeStamp = dateStr2timeStamp(dateStr)
    }

    fun onAddTask(){
        viewModelScope.launch {
            Log.i(TAG, "onAddTask: $task")
            task.value?.let {
                if (it.taskId == 0L)
                    insert(it)
                else
                    update(it)
            }
//            _navigateToHome.value = true
        }
    }

    fun setDate(date:String){
        dateString.value = date
    }

    private suspend fun insert(newTask: Task) {
        database.insert(newTask)
    }

    private suspend fun update(existTask:Task){
        database.update(existTask)
    }
}