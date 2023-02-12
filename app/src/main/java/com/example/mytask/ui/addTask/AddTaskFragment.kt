package com.example.mytask.ui.addTask

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mytask.DatePickerFragment
import com.example.mytask.R
import com.example.mytask.database.TaskDatabase
import com.example.mytask.databinding.FragmentAddTaskBinding

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
        addTaskViewModel.navigateToHome.observe(viewLifecycleOwner, Observer{
            if (it==true){
                this.findNavController().navigate(AddTaskFragmentDirections.actionAddTaskToNavHome())
            }
            addTaskViewModel.doneNavigating()
        })
        binding.addTaskViewModel = addTaskViewModel
//        binding.task =
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
            }
        )

        val saveButton = binding.imageButton
        saveButton.setOnClickListener {
        }

        datePickerListener()

        return binding.root
    }

    fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(childFragmentManager,"datePicker")
    }

    fun datePickerListener(){
        childFragmentManager.setFragmentResultListener("date_set",this) { key, bundle ->
            val result = bundle.getString("bundleKey")
            // Do something with the result
            Log.i(TAG, "showDatePickerDialog: date res $result")
            binding.editTextDate.setText(result)
            if (result != null) {
                addTaskViewModel.setStartTime(result)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}