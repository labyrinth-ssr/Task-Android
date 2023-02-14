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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mytask.DatePickerFragment
import com.example.mytask.R
import com.example.mytask.database.TaskDatabase
import com.example.mytask.databinding.FragmentAddTaskBinding
import com.example.mytask.databinding.TaskEditSubtasksBinding
import com.google.android.material.composethemeadapter.MdcTheme

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
        addTaskViewModel.task.observe(viewLifecycleOwner, Observer {
            taskName?.setText(it.taskName)
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

//private operator fun Role.invoke(painter: Painter, contentDescription: String, modifier: Modifier) {
//
//}
