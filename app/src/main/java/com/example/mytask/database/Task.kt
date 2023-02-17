package com.example.mytask.database

import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task{
    @PrimaryKey(autoGenerate = true)
    var taskId: Long = 0L
    @ColumnInfo(name = "parent_task_id")
    var parentTaskId: Long = 0L
    @ColumnInfo(name = "task_name")
    var taskName: String = "new"
    @ColumnInfo(name = "start_time_milli")
    var startTimeStamp: Long = System.currentTimeMillis()
    @ColumnInfo(name = "end_time_milli")
    var dueTimeStamp: Long = startTimeStamp
    @ColumnInfo(name = "priority")
    var priority: Int = Priority.LOW
    /** Unixtime Task was completed. 0 means active  */
    @ColumnInfo(name = "completed")
    var completionDate = 0L
    @ColumnInfo(name = "deleted")
    var deleted = false
    /** Unixtime Task was last touched  */
    @ColumnInfo(name = "modified")
    var modificationDate = 0L
    @ColumnInfo(name = "elapsedSeconds")
    var elapsedSeconds = 0
    @ColumnInfo(name = "timerStart")
    var timerStart = 0L
    @ColumnInfo(name = "calendarUri")
    var calendarURI: String? = null

    var isCompleted:Boolean = false
    var isRecurring:Boolean = false

    @IntDef(Priority.HIGH, Priority.MEDIUM, Priority.LOW, Priority.NONE)
    annotation class Priority {
        companion object {
            const val HIGH = 0
            const val MEDIUM = 1
            const val LOW = 2
            const val NONE = 3
        }
    }
}



