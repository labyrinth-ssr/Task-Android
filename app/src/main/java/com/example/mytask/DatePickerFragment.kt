package com.example.mytask

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.MutableLiveData
import com.example.mytask.database.Task
import com.example.mytask.ui.addTask.AddTaskViewModel
import java.time.LocalDate
import java.util.*

class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {
    val date = MutableLiveData<String>()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)

    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val sb = StringBuilder()
        sb.append(year).append("-").append(month).append("-").append(day)
        val result = sb.toString()
        Log.i(TAG, "onDateSet: " + result)

        setFragmentResult("date_set", bundleOf("bundleKey" to result))
    }
}