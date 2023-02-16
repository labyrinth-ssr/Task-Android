package com.example.mytask.ui.addTask

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.dateStr2timeStamp
import com.example.mytask.tasksToNodes
import com.example.mytask.ui.home.Node
import com.example.mytask.ui.home.TreeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Collections.addAll
import javax.annotation.Nonnull

class AddTaskViewModel(
    private val taskKey: Long = 0L,
    val database: TaskDatabaseDao) : ViewModel(), Observer<List<Node>> {

    val task = MutableLiveData<Task>()
    val subtasks = MutableLiveData<List<Node>?>()
    var newSubtasks = MutableStateFlow(emptyList<Task>())


    suspend fun taskKeyToNode(taskKey: Long) : MutableList<Node>{
        val taskList = ArrayList<Task>()
            .apply {
                addAll(
                    database.getChildren(taskKey)
                    .let { database.fetch(it) }
                )
            }
//        database.get(taskKey)?.let { taskList.add(it) }
        return TreeHelper.getSortedNodes(tasksToNodes(taskList))
    }

    init {
        if (taskKey != 0L){
            viewModelScope.launch {
                task.value = database.get(taskKey)
                subtasks.value = taskKeyToNode(taskKey)
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
        _navigateToHome.value = false
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

    fun setDueTime(dateStr:String){
        task.value?.dueTimeStamp = dateStr2timeStamp(dateStr)
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
            for (subtask in newSubtasks.value){
                subtask.parentTaskId = taskKey
                insert(subtask)
            }
            _navigateToHome.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
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

    override fun onChanged(t: List<Node>?) {
        subtasks.value = t
    }
}