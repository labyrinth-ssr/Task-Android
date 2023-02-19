package com.example.mytask.ui.home

import android.content.res.ColorStateList
import android.widget.CheckBox
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.DefaultTintColor
import androidx.databinding.BindingAdapter
import com.example.mytask.DateUtilities.dayStringFormat
import com.example.mytask.convertLongToDateString
import com.example.mytask.database.Task
import com.example.mytask.themes.ColorProvider


@BindingAdapter("dueTimeFormatted")
fun TextView.setDueTimeFormatted(item:Node?){
    item?.let {
        text = dayStringFormat(item.task.dueTimeStamp)
    }
}

@BindingAdapter("checkBoxDisplay")
fun CheckBox.setCheckBoxDisplay(item: Node?){
//    item?.let {
//        isChecked = item.isChecked
//    }
    item.let {
        if (item != null) {
            isChecked = item.task.isCompleted
            buttonTintList = ColorStateList.valueOf(ColorProvider.priorityColor(item.task.priority,false,true))
        };//setButtonTintList is accessible directly on API>19
    }
}

//@BindingAdapter("")
