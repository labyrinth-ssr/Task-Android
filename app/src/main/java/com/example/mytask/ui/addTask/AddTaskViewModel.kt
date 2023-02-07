package com.example.mytask.ui.addTask

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytask.DatePickerFragment
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import kotlinx.coroutines.launch

class AddTaskViewModel(
    private val taskKey: Long = 0L,
    val database: TaskDatabaseDao) : ViewModel() {

    private val task=Task()
//    lateinit var taskName:String
    fun onSetPriority(priority:Int) {
        task.priority = priority
//        viewModelScope.launch {
//            Log.i(TAG, "onSetPriority: taskKey$taskKey")
//            val task = database.get(taskKey) ?: return@launch
//            task.priority = priority
//            Log.i(TAG, "onSetPriority: view id$priority")
//            database.update(task)
//        }
    }

    fun getTask(): Task {
        return task;
    }

    fun setTaskName(taskName:String){
        task.taskName = taskName
    }

    fun onAddTask(){
        viewModelScope.launch {
            Log.i(TAG, "onAddTask: $task")
            insert(task)
        }
    }

    private suspend fun insert(newTask: Task) {
        database.insert(newTask)
    }
}