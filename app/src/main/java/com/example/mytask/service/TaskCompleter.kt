package com.example.mytask.service
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.now
import timber.log.Timber

class TaskCompleter (
    private val taskDao: TaskDatabaseDao,
) {
    suspend fun setComplete(taskId: Long) =
        taskDao
            .get(taskId)
            ?.let { setComplete(it, true) }
            ?: Timber.e("Could not find task $taskId")

    suspend fun setComplete(item: Task, completed: Boolean, includeChildren: Boolean = true) {
        val completionDate = if (completed) now() else 0L
        ArrayList<Task?>()
            .apply {
                if (includeChildren) {
                    addAll(taskDao.getChildren(item.taskId).let { taskDao.fetch(it) })
                }
                if (!completed) {
                    addAll(taskDao.getParents(item.taskId).let { taskDao.fetch(it) })
                }
                add(item)
            }
            .filterNotNull()
            .filter { it.isCompleted != completionDate > 0 }
            .let {
                setComplete(it, completionDate)
            }
    }

    suspend fun setComplete(tasks: List<Task>, completionDate: Long) {
        if (tasks.isEmpty()) {
            return
        }
        taskDao.setCompletionDate(tasks.map { it.taskId }, completionDate)
    }

    suspend fun delete(item: Task){
        ArrayList<Task?>()
            .apply {
                    addAll(taskDao.getChildren(item.taskId).let { taskDao.fetch(it) })
                    add(item)
            }.forEach {
                if (it != null) {
                    taskDao.delete(it.taskId)
                }
            }
    }
}