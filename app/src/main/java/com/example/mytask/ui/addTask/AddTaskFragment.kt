package com.example.mytask.ui.addTask

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mytask.DatePickerFragment
import com.example.mytask.DateUtilities
import com.example.mytask.R
import com.example.mytask.database.Task
import com.example.mytask.database.TaskDatabase
import com.example.mytask.databinding.FragmentAddTaskBinding
import com.example.mytask.databinding.TaskEditSubtasksBinding
import com.google.android.material.composethemeadapter.MdcTheme
import timber.log.Timber

class AddTaskFragment : Fragment() {
    // This property is only valid between onCreateView and
    // onDestroyView.

    lateinit var binding:FragmentAddTaskBinding;
    lateinit var addTaskViewModel: AddTaskViewModel;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(this.activity).application
        val arguments = AddTaskFragmentArgs.fromBundle(requireArguments())
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = AddTaskViewModelFactory(arguments.taskKey,dataSource)
        addTaskViewModel =
            ViewModelProvider(this,viewModelFactory).get(AddTaskViewModel::class.java)
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_add_task,container,false)
        binding.lifecycleOwner = this
        addTaskViewModel.navigateToHome.observe(viewLifecycleOwner, Observer{
            Timber.i("add navigate to home?"+it)
            if (it==true){
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



    override fun onDestroyView() {
        super.onDestroyView()
    }
}

//private operator fun Role.invoke(painter: Painter, contentDescription: String, modifier: Modifier) {
//
//}
