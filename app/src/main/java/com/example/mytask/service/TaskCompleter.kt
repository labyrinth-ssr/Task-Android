package com.example.mytask.service

import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import com.example.mytask.LocalBroadcastManager
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabaseDao
import com.example.mytask.jobs.WorkManager
import com.example.mytask.now
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class TaskCompleter @Inject internal constructor(
//    @ApplicationContext private val context: Context,
    private val taskDao: TaskDatabaseDao,
//    private val notificationManager: NotificationManager,
    private val localBroadcastManager: LocalBroadcastManager,
//    private val workManager: WorkManager,
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
//                    addAll(googleTaskDao.getChildTasks(item.id))
                    addAll(taskDao.getChildren(item.taskId).let { taskDao.fetch(it) })
                }
                if (!completed) {
//                    add(googleTaskDao.getParentTask(item.id))
                    addAll(taskDao.getParents(item.taskId).let { taskDao.fetch(it) })
                }
                add(item)
            }
            .filterNotNull()
            .filter { it.isCompleted != completionDate > 0 }
//            .filterNot { it.readOnly }
            .let {
                setComplete(it, completionDate)
//                if (completed && !item.isRecurring) {
//                    localBroadcastManager.broadcastTaskCompleted(ArrayList(it.map(Task::taskId)))
//                }
            }
    }

    suspend fun setComplete(tasks: List<Task>, completionDate: Long) {
        if (tasks.isEmpty()) {
            return
        }
        val completed = completionDate > 0
        taskDao.setCompletionDate(tasks.map { it.taskId }, completionDate)
        tasks.forEachIndexed { i, original ->
//            if (i < tasks.size - 1) {
//                original.suppressRefresh()
//            }
            taskDao.update(original)
        }
        tasks.forEach {
//            if (completed && it.isRecurring) {
//                workManager.scheduleRepeat(it)
//            }
        }
//        if (completed && notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
//            preferences
//                .completionSound
//                ?.takeUnless { preferences.isCurrentlyQuietHours }
//                ?.let { RingtoneManager.getRingtone(context, it).play() }
//        }
    }
}