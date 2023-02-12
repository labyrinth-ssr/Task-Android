package com.example.mytask.ui.home

import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.mytask.convertLongToDateString
import com.example.mytask.database.Task


@BindingAdapter("dueTimeFormatted")
fun TextView.setDueTimeFormatted(item:Node?){
    item?.let {
        text = convertLongToDateString(item.task.dueTimeStamp)
    }
}

@BindingAdapter("checkBoxDisplay")
fun CheckBox.setCheckBoxDisplay(item: Node?){
    item?.let {
        isChecked = item.isChecked
    }
}

//@BindingAdapter("")
