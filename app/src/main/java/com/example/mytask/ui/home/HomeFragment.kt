package com.example.mytask.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mytask.R
import com.example.mytask.database.TaskDatabase
import com.example.mytask.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    /**
     * 和view进行数据绑定
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adapter = TaskAdapter()
        val binding:FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = HomeViewModelFactory(dataSource, application)
        val homeViewModel =
            ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)

        homeViewModel.navigateToAddTask.observe(viewLifecycleOwner, Observer {
            task->
            task?.let {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToAddTask(task.taskId)
                )
                homeViewModel.doneNavigating()
                Log.i(TAG, "onCreateView: task id"+task.taskId)
            }
        })
        binding.lifecycleOwner = this
        binding.homeViewModel = homeViewModel
        binding.taskList.adapter = adapter
        homeViewModel.tasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        binding = null
    }
}