package com.example.mytask

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.icu.text.RelativeDateTimeFormatter
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.mytask.database.Task
import com.example.mytask.ui.home.Node
import java.lang.System.currentTimeMillis
import java.text.ParsePosition
import java.text.SimpleDateFormat


/**
 * Take the Long milliseconds returned by the system and stored in Room,
 * and convert it to a nicely formatted string for display.
 *
 * @param systemTime
 * @return
 */
@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm")
        .format(systemTime).toString()
}

fun now(): Long {
    return currentTimeMillis()
}

/**
 * Takes a list of SleepNights and converts and formats it into one string for display.
 *
 * @param tasks
 * @return
 */
fun formatTasks(tasks: List<Task>): String {
    val sb = StringBuilder()
    sb.apply {
        tasks.forEach {
            append(it.taskName)
            append("\t${it.priority}")
            append("\t${convertLongToDateString(it.startTimeStamp)}")
            append("\n")
        }
    }
    Log.i(TAG, "formatTasks: $sb")
    return sb.toString()
}

fun formatTask(task: Task): String {
    val sb = StringBuilder()
    sb.apply {
            append(task.taskName)
            append("\t${task.priority}")
            append("\t${convertLongToDateString(task.startTimeStamp)}")
            append("\n")
    }
    Log.i(TAG, "formatTask: $sb")
    return sb.toString()
}

fun dateStr2timeStamp(dateStr : String) : Long{
    val pattern = "yyyy-MM-dd"
    val simpleDateFormat = SimpleDateFormat(pattern)
    val date = simpleDateFormat.parse(dateStr)
    val timeStamp = date.time
    return timeStamp
}

fun transToString(time:Long):String{
    return SimpleDateFormat("yy-MM-DD-hh-mm-ss").format(time)
}
fun transToTimeStamp(date:String):Long{
    return SimpleDateFormat("yy-MM-DD-hh-mm-ss").parse(date, ParsePosition(0)).time
}

fun tasksToNodes(tasks: List<Task>?):MutableList<Node> {
    val nodes:MutableList<Node> = mutableListOf()
    tasks?.forEach {
        nodes.add(Node(it))
    }
    return nodes
}

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

fun <T : Parcelable> deepClone(objectToClone: T): T? {
    var parcel: Parcel? = null
    return try {
        parcel = Parcel.obtain()
        parcel.writeParcelable(objectToClone, 0)
        parcel.setDataPosition(0)
        parcel.readParcelable(objectToClone::class.java.classLoader)
    } finally {
        //it is important to recyle parcel and free up resources once done.
        parcel?.recycle()
    }
}
