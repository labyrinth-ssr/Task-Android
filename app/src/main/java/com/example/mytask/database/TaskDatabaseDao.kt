package com.example.mytask.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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
    @Query("SELECT * FROM task_table ORDER BY taskId DESC")
    suspend fun getDueTodayTask(): Task?
    @Query("SELECT * FROM task_table ORDER BY taskId DESC")
    fun getAllTasks(): LiveData<List<Task>>
    @Query("SELECT * from task_table WHERE taskId = :key")
    fun getTaskWithId(key: Long): LiveData<Task>
}