package com.example.mytask.ui.addTask

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.mytask.MyTasksApplication
import com.example.mytask.R
import com.example.mytask.calendar.PermissionChecker
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.dateStr2timeStamp
import com.example.mytask.service.TaskCompleter
import com.example.mytask.tasksToNodes
import com.example.mytask.ui.home.Node
import com.example.mytask.ui.home.TreeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.AccessController.getContext
import java.util.*
import java.util.Collections.addAll
import javax.annotation.Nonnull
import kotlin.collections.ArrayList

class AddTaskViewModel (
    private val taskKey: Long = 0L,
    private val permissionChecker: PermissionChecker,
    val database: TaskDatabaseDao ) : ViewModel(), Observer<List<Node>> {
    lateinit var taskCompleter: TaskCompleter
    val task = MutableLiveData<Task>()
    val subtasks = MutableLiveData<List<Node>?>()
    var newSubtasks = MutableStateFlow(emptyList<Task>())
    var timerStarted = MutableStateFlow(0L)
    var elapsedSeconds = MutableStateFlow(0)
    var intent = Intent()

    var eventUri = MutableStateFlow(task.value?.calendarURI)
    val isNew = taskKey == 0L
    @RequiresApi(Build.VERSION_CODES.M)
    private var originalCalendar: String? = if (isNew && permissionChecker.canAccessCalendars()) {
        "default_calendar_id"
    } else {
        null
    }
    @RequiresApi(Build.VERSION_CODES.M)
    var selectedCalendar = MutableStateFlow(originalCalendar)

    fun insertCalendar(){
        intent = task.value?.let {
            Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, it.startTimeStamp)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it.dueTimeStamp)
                .putExtra(CalendarContract.Events.TITLE, it.taskName)
    //                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
    //                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        }!!
    }

    suspend fun setComplete(task1: Task,completed:Boolean){
        taskCompleter.setComplete(task1, completed)
        subtasks.value = taskKeyToNode(taskKey)
        task.value = database.get(taskKey)
    }

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
                subtasks.value = taskKeyToNode(taskKey)
                task.value = database.get(taskKey)
                timerStarted = MutableStateFlow(task.value!!.timerStart)
                elapsedSeconds = MutableStateFlow(task.value!!.elapsedSeconds)
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

    fun setTaskName(taskName:String){
        task.value?.taskName = taskName
    }

    fun setStartTime(dateStr:String){
        task.value?.startTimeStamp = dateStr2timeStamp(dateStr)
    }

    fun setDueTime(dateStr:String){
        task.value?.dueTimeStamp = dateStr2timeStamp(dateStr)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onAddTask(){
        viewModelScope.launch {
            Log.i(TAG, "onAddTask: $task")
            task.value?.let {
                Timber.i("selected calendar:"+selectedCalendar)
                if (selectedCalendar != null){
//                    it.calendarURI =
                }
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

    fun onDeleteTask(){
        viewModelScope.launch {
            task.value?.let { taskCompleter.delete(it) }
            _navigateToHome.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
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