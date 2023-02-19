package com.example.mytask.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytask.R
import com.example.mytask.database.Task
import com.example.mytask.databinding.ListItemTaskBinding
import com.example.mytask.themes.ColorProvider
import timber.log.Timber

class TaskAdapter(val clickListener: TaskListener) : ListAdapter<Node,RecyclerView.ViewHolder>(TaskDiffCallback()) {
    /**
     * 存储所有可见的Node
     */
    var mNodesLive: MutableLiveData<MutableList<Node>> = MutableLiveData()
    lateinit var viewModel: HomeViewModel

//    var nodeList:MutableList<Node> = ArrayList<Node>()
    lateinit var mAllNodes: MutableList<Node>
//    = TreeHelper.getSortedNodes(nodeList)
//    override fun getItemCount() = mNodes.size
//    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
//        val item = data[position]
//        holder.textView.text = formatTask(item)
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val view = layoutInflater
//            .inflate(R.layout.list_item_task, parent, false)
//        return TextItemViewHolder(view)
        return InnerViewHolder.from(parent,viewType)
//        return InnerViewHolder(view)
    }
//
    class InnerViewHolder(val binding:ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        val itemLayout = binding.itemLayout
        val icon = binding.idTreenodeIcon
        val checkBox = binding.idTreeNodeCheck
        val name = binding.idTreenodeName
    fun bind(item: Node, clickListener: TaskListener) {
        binding.node = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

        companion object {
            fun from(parent: ViewGroup,viewType:Int): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTaskBinding.inflate(layoutInflater, parent, false)
                val view = binding.root
                binding.root.setPadding(viewType * 60, 3, 3, 3)
                return InnerViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        Timber.i("node id:"+mNodesLive.value!![position].id+" level:"+mNodesLive.value!![position].level)
        return mNodesLive.value!![position].level
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mNodes = mNodesLive.value!!
        val innerViewHolder = holder as InnerViewHolder
        innerViewHolder.bind(getItem(position)!!, clickListener)
        innerViewHolder.icon.visibility = View.VISIBLE
        Timber.i("bind view:"+mNodes[position].name+" "+mNodes[position].isExpand)
        if(mNodes[position].isExpand && mNodes[position].childrenNodes.size > 0) {
            innerViewHolder.icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
        } else if(!mNodes[position].isExpand && mNodes[position].childrenNodes.size > 0) {
            innerViewHolder.icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        } else {
            innerViewHolder.icon.visibility = View.GONE
        }

//        innerViewHolder.checkBox.isChecked = mNodes[position].isChecked
        innerViewHolder.name.text = mNodes[position].name

        innerViewHolder.checkBox.setOnClickListener {
            val isChecked = !mNodes[position].isChecked
            TreeHelper.setNodeChecked(mNodes[position], isChecked)
            viewModel.setComplete(mNodes[position].task,isChecked)
            notifyDataSetChanged()
        }

        innerViewHolder.icon.setOnClickListener {
            if(!mNodes[position].isExpand && mNodes[position].childrenNodes.size > 0) {
                innerViewHolder.icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else if(mNodes[position].isExpand && mNodes[position].childrenNodes.size > 0) {
                innerViewHolder.icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
            expandOrCollapse(position)
//            onTreeNodeClickListener?.onTreeItemClick(mNodes[position], position)
        }
    }


    /**
     * 相应ListView的点击事件 展开或关闭某节点
     */
    private fun expandOrCollapse(position: Int) {
        val n = mNodesLive.value!![position]
        Timber.i("size:"+n.childrenNodes.size)
        n.let {
            if (!n.isLeaf) {
                n.isExpand = !n.isExpand
                mNodesLive.value = TreeHelper.filterVisibleNode(mAllNodes)
                mNodesLive.value!!.forEach {
                    Timber.i(it.name+" expand? "+it.isExpand)
                }
                currentList.forEach {
                    Timber.i(it.name+"current expand? "+it.isExpand)
                }
//                submitList(mNodesLive.value!!)
            }
        }
    }

    fun deleteSelectedNode() {
        val selectedIdList = getSelectedNodeListId()
        deleteNode(selectedIdList, mAllNodes)
        updateNodeList(selectedIdList, mAllNodes)
        deleteNode(selectedIdList, mNodesLive.value!!)
        updateNodeList(selectedIdList, mNodesLive.value!!)
        submitList(mNodesLive.value!!)
    }

    private fun getSelectedNodeListId(): MutableList<Int> {
        val selectedList:MutableList<Int> = mutableListOf()
        for (node in mAllNodes) {
            if (node.isChecked) {
                if (!selectedList.contains(node.id)) selectedList.add(node.id)
            }
        }
        return selectedList
    }

    private fun deleteNode(selectedNodeListId: MutableList<Int>, nodeList: MutableList<Node>) {
        val iterator = nodeList.iterator()
        while (iterator.hasNext()) {
            val node = iterator.next()
            if (selectedNodeListId.contains(node.id)) {
                iterator.remove()
            }
        }
    }

    private fun updateNodeList(selectedIdList: MutableList<Int>, nodeList : MutableList<Node>) {
        for (node in nodeList) {
            deleteUnusedNodeInfo(selectedIdList, node)
        }
    }

    /**
     * 删除操作，更新每个节点中已被删除的信息
     */
    private fun deleteUnusedNodeInfo(selectedIdList: MutableList<Int>, node: Node) {
        val childrenIterator = node.childrenNodes.iterator()
        while (childrenIterator.hasNext()) {
            val delChildrenNode = childrenIterator.next()
            if (selectedIdList.contains(delChildrenNode.id)) {
                childrenIterator.remove()
            }
            if (!delChildrenNode.isLeaf) {
                deleteUnusedNodeInfo(selectedIdList, delChildrenNode)
            }
        }
    }

//    private var onTreeNodeClickListener: OnTreeNodeClickListener? = null

//    interface OnTreeNodeClickListener {
//        fun onTreeItemClick(node: Node, position: Int)
//    }

//    fun setOnTreeNodeClickListener(onTreeNodeClickListener: HomeFragment) {
//        this.onTreeNodeClickListener = onTreeNodeClickListener
//    }

//    init {
//        /**
//         * 过滤出可见的Node
//         */
//        mNodes = TreeHelper.filterVisibleNode(mAllNodes)
//    }

}

class TaskDiffCallback:DiffUtil.ItemCallback<Node>(){
    override fun areItemsTheSame(oldItem: Node, newItem: Node): Boolean {
        Timber.i("item "+oldItem.name+" equal? :"+(oldItem == newItem)+" expand?:"+oldItem.isExpand+" new:"+newItem.isExpand)
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Node, newItem: Node): Boolean {
        Timber.i("item "+oldItem.name+"content equal? :"+(oldItem == newItem)+" expand?:"+oldItem.isExpand+" new:"+newItem.isExpand)
        return oldItem == newItem
    }
}

class TaskListener(val clickListener: (task:Task)->Unit){
    fun onCLick(task:Task)= clickListener(task)
}

class CheckboxListener(val checkboxListener: ()->Unit)


