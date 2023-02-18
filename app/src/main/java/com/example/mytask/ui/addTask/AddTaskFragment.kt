package com.example.mytask.ui.addTask

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.foundation.layout.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mytask.DatePickerFragment
import com.example.mytask.DateUtilities
import com.example.mytask.R
import com.example.mytask.calendar.CalendarPicker
import com.example.mytask.calendar.PermissionChecker
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabase
import com.example.mytask.databinding.FragmentAddTaskBinding
import com.example.mytask.databinding.TaskEditCalendarBinding
import com.example.mytask.databinding.TaskEditSubtasksBinding
import com.example.mytask.databinding.TaskEditTimerBinding
import com.example.mytask.time.TimerPlugin
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AddTaskFragment : Fragment(){

    lateinit var timerPlugin: TimerPlugin
    @Inject lateinit var context: Activity
    @Inject lateinit var permissionChecker: PermissionChecker
    // This property is only valid between onCreateView and
    // onDestroyView.

    lateinit var binding:FragmentAddTaskBinding;
    lateinit var addTaskViewModel: AddTaskViewModel;

    fun insertCalendar(){
        val startMillis: Long = Calendar.getInstance().run {
            set(2023, 1, 18, 7, 30)
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(2023, 1, 18, 8, 30)
            timeInMillis
        }
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            .putExtra(CalendarContract.Events.TITLE, "Yoga")
            .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
            .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//            .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
//        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(this.activity).application
        val arguments = AddTaskFragmentArgs.fromBundle(requireArguments())
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = AddTaskViewModelFactory(arguments.taskKey,dataSource, permissionChecker)
        timerPlugin = TimerPlugin(dataSource)
        addTaskViewModel =
            ViewModelProvider(this,viewModelFactory).get(AddTaskViewModel::class.java)
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_add_task,container,false)
        binding.lifecycleOwner = this
        addTaskViewModel.navigateToHome.observe(viewLifecycleOwner, Observer{
            Timber.i("add navigate to home?"+it)
            if (it==true){
                insertCalendar()
                this.findNavController().navigate(AddTaskFragmentDirections.actionAddTaskToNavHome())
                addTaskViewModel.doneNavigating()
            }
        })

        binding.addTaskViewModel = addTaskViewModel

        val dueTime = binding.editTextDate2
        val beginTime = binding.editTextDate
        val priority = binding.prioOptions
        beginTime.isFocusable = false
        beginTime.setOnClickListener(){
            showDatePickerDialog("pick_begin_time")
        }
        dueTime.isFocusable = false
        dueTime.setOnClickListener(){
            showDatePickerDialog("pick_due_time")
        }

        val taskName = binding.taskName.editText
        taskName?.addTextChangedListener(
            onTextChanged = {_, _, _, _ ->
                addTaskViewModel.setTaskName(taskName.text.toString().trim { it <= ' ' })
            }
        )
        addTaskViewModel.task.observe(viewLifecycleOwner, Observer {
            taskName?.setText(it.taskName)
            beginTime.setText(DateUtilities.dayStringFormat(it.startTimeStamp))
            dueTime.setText(DateUtilities.dayStringFormat(it.dueTimeStamp))
            var id = R.id.prio_low
            when (it.priority){
                Task.Priority.NONE -> id = R.id.prio_none
                Task.Priority.LOW -> id = R.id.prio_low
                Task.Priority.MEDIUM -> id = R.id.prio_medium
                Task.Priority.HIGH -> id = R.id.prio_high
            }
            priority.check(id)
        })

        val saveButton = binding.imageButton
        saveButton.setOnClickListener {
        }

        datePickerListener()


        binding.composeView.setContent { 
            MdcTheme {
                Column {
                    AndroidViewBinding(TaskEditSubtasksBinding::inflate)
                    AndroidViewBinding(TaskEditTimerBinding::inflate)
                    AndroidViewBinding(TaskEditCalendarBinding::inflate)
                }
            }
        }

        return binding.root
    }

    fun showDatePickerDialog(tag:String) {
        val newFragment = DatePickerFragment()
        newFragment.show(childFragmentManager,tag)
    }

    fun datePickerListener(){


        childFragmentManager.setFragmentResultListener("begin_date_set",this) { key, bundle ->
            val result = bundle.getString("bundleKey")
            Log.i(TAG, "showDatePickerDialog: date res $result")
            binding.editTextDate.setText(result)
            if (result != null) {
                addTaskViewModel.setStartTime(result)
            }
        }

        childFragmentManager.setFragmentResultListener("due_date_set",this) { key, bundle ->
            val result = bundle.getString("bundleKey")
            Log.i(TAG, "showDatePickerDialog: date res $result")
            binding.editTextDate2.setText(result)
            if (result != null) {
                addTaskViewModel.setDueTime(result)
            }
        }
//            .setFragmentResult("date_set")
//        childFragmentManager.setFragmentResultListener("date_set",this) { key, bundle ->
//            val result = bundle.getString("bundleKey")
//            // Do something with the result
//            Log.i(TAG, "showDatePickerDialog: date res $result")
//            binding.editTextDate.setText(result)
//            if (result != null) {
//                addTaskViewModel.setStartTime(result)
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            70 -> {
                if (resultCode == Activity.RESULT_OK) {
                    addTaskViewModel.selectedCalendar.value =
                        data!!.getStringExtra(CalendarPicker.EXTRA_CALENDAR_ID)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
    }

    suspend fun stopTimer(): Task {
        val model = addTaskViewModel.task.value!!
        timerPlugin.stopTimer(model)
//        addComment(String.format(
//            "%s %s",
//            getString(R.string.TEA_timer_comment_started),
//            DateUtilities.getTimeString(context, newDateTime())),
//            null)
        return model
    }

    suspend fun startTimer(): Task {
        val model = addTaskViewModel.task.value!!
        timerPlugin.startTimer(model)
//        editViewModel.addComment(String.format(
//            "%s %s",
//            getString(R.string.TEA_timer_comment_started),
//            DateUtilities.getTimeString(context, newDateTime())),
//            null)
        return model
    }
}

//private operator fun Role.invoke(painter: Painter, contentDescription: String, modifier: Modifier) {
//
//}
