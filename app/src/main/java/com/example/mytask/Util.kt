package com.example.mytask

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mytask.database.Task
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
    return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
        .format(systemTime).toString()
}


/**
 * Takes a list of SleepNights and converts and formats it into one string for display.
 *
 * @param tasks
 * @param resources
 * @return
 */
fun formatTasks(tasks: List<Task>, resources: Resources): String {
    val sb = StringBuilder()
    sb.apply {
        tasks.forEach {
            append(it.taskName)
            append("\t${it.priority}")
            append("\t${convertLongToDateString(it.startTimeMilli)}")
            append("\n")
        }
    }
    Log.i(TAG, "formatTasks: $sb")
    return sb.toString()
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
//    } else {
//        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
//    }
}

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)
