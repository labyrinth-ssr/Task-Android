package com.example.mytask.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytask.R
import com.example.mytask.database.TaskDatabase
import com.example.mytask.databinding.FragmentHomeBinding
import com.example.mytask.tasksToNodes
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : Fragment(){
    /**
     * 和view进行数据绑定
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    private lateinit var adapter: TaskAdapter
    lateinit var binding:FragmentHomeBinding
    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = HomeViewModelFactory(dataSource, application)
        homeViewModel =
            ViewModelProvider(this,viewModelFactory)[HomeViewModel::class.java]
        binding.lifecycleOwner = this
        binding.homeViewModel = homeViewModel
        adapter = TaskAdapter(TaskListener { task ->
            homeViewModel.onTaskClicked(task)
        })
        binding.taskList.layoutManager = LinearLayoutManager(activity)
        binding.taskList.adapter = adapter

        adapter.mNodesLive.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        homeViewModel.navigateToAddTask.observe(viewLifecycleOwner, Observer {

            task->
            task?.let {
                Timber.i("home navigate to add?"+it.taskName)
                this.findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToAddTask(task.taskId)
                )
                homeViewModel.doneNavigating()
                Log.i(TAG, "onCreateView:navigate task id"+task.taskId)
            }
        })

//        homeViewModel.

        homeViewModel.tasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.mAllNodes = TreeHelper.getSortedNodes(tasksToNodes(it))
                Timber.i(adapter.mAllNodes.size.toString())
                adapter.mNodesLive.value = TreeHelper.filterVisibleNode(adapter.mAllNodes)
                adapter.mNodesLive.value!!.forEach {
                    Timber.i(it.toString())
                }
//                lifecycleScope.launch()
//                adapter.submitList(adapter.mNodes)
            }
//            if (it.isEmpty())
//                Log.i(TAG, "onCreateView: no tasks")
//            else Log.i(TAG, "onCreateView: "+it[0].taskName)
//            Log.i(TAG, "onCreateView: observe item change")
        })


//        initRecyclerView()
        return binding.root
    }

//    private fun initRecyclerView() {
////        mNodeList.clear()
////        homeViewModel.tasks.value?.forEach {
////            mNodeList.add(Node(it))
////        }
////        if (homeViewModel.tasks.value==null){
////            Log.i(TAG, "initRecyclerView: task null")
////        }
//
////        adapter.setOnTreeNodeClickListener(this)
////        binding.taskList.layoutManager = LinearLayoutManager(activity)
////        binding.taskList.adapter = adapter
//    }

    override fun onDestroyView() {
        super.onDestroyView()
//        binding = null
    }

//    override fun onTreeItemClick(node: Node, position: Int) {
//        if (node.isLeaf) {
//            Toast.makeText(
//                activity?.applicationContext, node.name,
//                Toast.LENGTH_SHORT).show()
//        }
//    }
}