package com.example.mytask.ui.addTask

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mytask.DatePickerFragment
import com.example.mytask.R
import com.example.mytask.database.TaskDatabase
import com.example.mytask.databinding.FragmentAddTaskBinding

class AddTaskFragment : Fragment() {
    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(this.activity).application
        val arguments = AddTaskFragmentArgs.fromBundle(requireArguments())
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = AddTaskViewModelFactory(arguments.taskKey,dataSource)
        val addTaskViewModel =
            ViewModelProvider(this,viewModelFactory).get(AddTaskViewModel::class.java)
        val binding: FragmentAddTaskBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_task,container,false)
        binding.addTaskViewModel = addTaskViewModel
        binding.editTextDate.isFocusable = false
        binding.editTextDate.setOnClickListener(){
            showDatePickerDialog()
        }
        binding.editTextDate2.isFocusable = false
        binding.editTextDate2.setOnClickListener(){
            showDatePickerDialog()
        }

        val taskName = binding.taskName.editText

        taskName?.addTextChangedListener(
            onTextChanged = {_, _, _, _ ->
                addTaskViewModel.setTaskName(taskName.text.toString().trim { it <= ' ' })
//                addTaskViewModel.getTask().taskName = taskName.text.toString().trim { it <= ' ' }
//                Log.i(TAG, "onCreateView: task name "+ addTaskViewModel.getTask().taskName)
            }
    //                ,
    //                afterTextChanged = {
    //                    textWatcher?.invoke(it)
    //                }
        )
        val saveButton = binding.imageButton
        saveButton.setOnClickListener {

        }
//        taskName?.setText(addTaskViewModel.taskName)

        return binding.root
    }

    fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(parentFragmentManager,"datePicker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}