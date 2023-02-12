package com.example.mytask.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var taskId: Long = 0L,
    @ColumnInfo(name = "parent_task_id")
    var parentTaskId: Long = 0L,
    @ColumnInfo(name = "task_name")
    var taskName: String = "new",
    @ColumnInfo(name = "start_time_milli")
    var startTimeStamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "end_time_milli")
    var dueTimeStamp: Long = startTimeStamp,
    @ColumnInfo(name = "priority")
    var priority:Int = 2
)

