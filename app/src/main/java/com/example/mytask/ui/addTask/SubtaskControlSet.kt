package com.example.mytask.ui.addTask

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.example.mytask.CheckBoxProvider
import com.example.mytask.R
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.tasksToNodes
import com.example.mytask.themes.ColorProvider
import com.example.mytask.ui.home.Node
import com.example.mytask.ui.home.TreeHelper
import com.google.android.material.composethemeadapter.MdcTheme
import javax.inject.Inject

class SubtaskControlSet: TaskEditControlFragment() {
//    @Inject lateinit var activity: Activity
//    @Inject lateinit var taskCompleter: TaskCompleter
//    @Inject lateinit var googleTaskDao: GoogleTaskDao
//    @Inject lateinit var taskCreator: TaskCreator
    @Inject lateinit var taskDao: TaskDatabaseDao
    @Inject lateinit var checkBoxProvider: CheckBoxProvider
//    @Inject lateinit var chipProvider: ChipProvider
//    @Inject lateinit var eventBus: MainActivityEventBus
    @Inject lateinit var colorProvider: ColorProvider
//    @Inject lateinit var preferences: Preferences
//    lateinit var listViewModel: TaskListViewModel

    override fun createView(savedInstanceState: Bundle?) {
//        viewModel.task.takeIf { it.id > 0 }?.let {
//            listViewModel.setFilter(Filter("subtasks", getQueryTemplate(it)))
//        }
    }

    fun getExistingSubtask( subtasks:List<Task>):List<Task>?{
        val taskList:MutableList<Task> = viewModel.subtasks.value?.toMutableList() ?: ArrayList<Task>()
        viewModel.task.value?.let { taskList.add(it) }
        return taskList
    }

    override fun bind(parent: ViewGroup?): View =
        (parent as ComposeView).apply {
//            listViewModel = ViewModelProvider(requireParentFragment())[TaskListViewModel::class.java]
            setContent {
                MdcTheme {
                    SubtaskRow(
//            filter = null,
//            googleTask = null,
//            desaturate = true,
                        existingSubtasks = Node.getNodeById (TreeHelper.getSortedNodes(tasksToNodes(
                            viewModel.subtasks.value?.let { getExistingSubtask(it) })),viewModel.task.value!!.taskId),
                        newSubtasks = listOf(
                            Task().apply {
                                taskName = "New subtask 1"
                            },
                            Task().apply {
                                taskName = "New subtask 2 with a really long title"
                            },
                            Task(),
                        ),
                        openSubtask = {},
                        completeExistingSubtask = { _, _ -> },
                        completeNewSubtask = {},
                        toggleSubtask = { _, _ -> },
                        addSubtask = {},
                    ) {}

//                    SubtaskRow(
////                        filter = viewModel.selectedList.collectAsStateLifecycleAware().value,
////                        googleTask = googleTaskDao.watchGoogleTask(viewModel.task.id).collectAsStateLifecycleAware(initial = null).value,
////                        desaturate = preferences.desaturateDarkMode,
//                        existingSubtasks = listViewModel.tasks.observeAsState(initial = emptyList()).value,
//                        newSubtasks = viewModel.newSubtasks.collectAsStateLifecycleAware().value,
//                        openSubtask = this@SubtaskControlSet::openSubtask,
//                        completeExistingSubtask = this@SubtaskControlSet::complete,
//                        toggleSubtask = this@SubtaskControlSet::toggleSubtask,
//                        addSubtask = this@SubtaskControlSet::addSubtask,
//                        completeNewSubtask = {
//                            viewModel.newSubtasks.value =
//                                ArrayList(viewModel.newSubtasks.value).apply {
//                                    val modified = it.clone().apply {
//                                        completionDate =
//                                            if (isCompleted) 0 else now()
//                                    }
//                                    set(indexOf(it), modified)
//                                }
//                        },
//                        deleteSubtask = {
//                            viewModel.newSubtasks.value =
//                                ArrayList(viewModel.newSubtasks.value).apply {
//                                    remove(it)
//                                }
//                        }
//                    )
                }
            }
        }

    override fun controlId() = TAG
//
//    private fun addSubtask() = lifecycleScope.launch {
//        val task = taskCreator.createWithValues("")
//        viewModel.newSubtasks.value = viewModel.newSubtasks.value.plus(task)
//    }
//
//    private fun openSubtask(task: Task) = lifecycleScope.launch {
//        eventBus.emit(MainActivityEvent.OpenTask(task))
//    }
//
//    private fun toggleSubtask(taskId: Long, collapsed: Boolean) = lifecycleScope.launch {
//        taskDao.setCollapsed(taskId, collapsed)
//    }
//
//    private fun complete(task: Task, completed: Boolean) = lifecycleScope.launch {
//        taskCompleter.setComplete(task, completed)
//    }

    companion object {
        const val TAG = R.string.subtask_tag
//        private fun getQueryTemplate(task: Task): QueryTemplate = QueryTemplate()
//            .join(
//                Paint.Join.left(
//                    GoogleTask.TABLE,
//                    Criterion.and(
//                        GoogleTask.PARENT.eq(task.id),
//                        GoogleTask.TASK.eq(Task.ID),
//                        GoogleTask.DELETED.eq(0)
//                    )
//                )
//            )
//            .where(
//                Criterion.and(
//                    activeAndVisible(),
//                    Criterion.or(Task.PARENT.eq(task.id), GoogleTask.TASK.gt(0))
//                )
//            )
    }
}