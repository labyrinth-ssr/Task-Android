package com.example.mytask.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mytask.database.SuspendDbUtils.chunkedMap
import com.example.mytask.now
import dagger.Binds

//import org.todoroo.andlib.utility.com.example.mytask.DateUtilities.now

@Dao
interface TaskDatabaseDao {
    @Insert
    suspend fun insert(task:Task)
    @Update
    suspend fun update(task: Task)
    @Query("SELECT * from task_table WHERE taskId = :key")
    suspend fun get(key:Long):Task?
    @Query("DELETE FROM task_table")
    suspend fun clear()
    @Query("DELETE FROM task_table where taskId = :key")
    suspend fun delete(key: Long)
    @Query("SELECT * FROM task_table ORDER BY taskId DESC")
    suspend fun getDueTodayTask(): Task?
    @Query("SELECT * FROM task_table ORDER BY taskId DESC")
    fun getAllTasks(): LiveData<List<Task>>
//    @Query("SELECT * from task_table WHERE taskId = :key")
//    fun getTask(key: Long): MutableLiveData<Task>

    @Query("UPDATE task_table SET completed = :completionDate, modified = :updateTime WHERE taskId IN (:taskIds)")
    abstract suspend fun setCompletionDate(taskIds: List<Long>, completionDate: Long, updateTime: Long = now())
    @Query("""
    WITH RECURSIVE recursive_tasks (task, parent) AS (
        SELECT taskId, parent_task_id FROM task_table WHERE taskId = :parent
        UNION ALL
        SELECT taskId, task_table.parent_task_id FROM task_table
            INNER JOIN recursive_tasks ON recursive_tasks.parent = task_table.taskId
        WHERE task_table.deleted = 0
    )
    SELECT task
    FROM recursive_tasks
    """)
    abstract suspend fun getParents(parent: Long): List<Long>
    suspend fun getChildren(id: Long): List<Long> = getChildren(listOf(id))
    @Query("""
    WITH RECURSIVE recursive_tasks (task) AS (
        SELECT taskId
        FROM task_table
        WHERE parent_task_id IN (:ids)
        UNION ALL
        SELECT taskId
        FROM task_table
                 INNER JOIN recursive_tasks ON recursive_tasks.task = task_table.parent_task_id
        )
    SELECT task
    FROM recursive_tasks
        """)
    abstract suspend fun getChildren(ids: List<Long>): List<Long>

    @Query("SELECT * FROM task_table WHERE taskId IN (:ids)")
    abstract suspend fun fetchInternal(ids: List<Long>): List<Task>

    suspend fun fetch(ids: List<Long>): List<Task> = ids.chunkedMap(this::fetchInternal)

//    suspend fun setComplete(id: Long)
}