package com.example.mytask.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytask.R
import com.example.mytask.TextItemViewHolder
import com.example.mytask.database.Task
import com.example.mytask.databinding.ListItemTaskBinding
import com.example.mytask.formatTask
//import com.example.mytask.generated.callback.OnClickListener

class TaskAdapter(val clickListener: TaskListener) : ListAdapter<Node,RecyclerView.ViewHolder>(TaskDiffCallback()) {
    /**
     * 存储所有可见的Node
     */
    lateinit var mNodes: MutableList<Node>
//    var nodeList:MutableList<Node> = ArrayList<Node>()
    lateinit var mAllNodes: MutableList<Node>
//    = TreeHelper.getSortedNodes(nodeList)
//    override fun getItemCount() = data.size
//    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
//        val item = data[position]
//        holder.textView.text = formatTask(item)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_task, parent, false)
        view.setPadding(viewType * 60, 3, 3, 3)
//        return TextItemViewHolder(view)
        return InnerViewHolder.from(parent)
//        return InnerViewHolder(view)
    }
//
    class InnerViewHolder(val binding:ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemLayout = binding.itemLayout
//            itemView.findViewById<ConstraintLayout>(R.id.item_layout)
        val icon = binding.idTreenodeIcon
//            itemView.findViewById<ImageView>(R.id.id_treenode_icon)
        val checkBox = binding.idTreeNodeCheck
//            itemView.findViewById<CheckBox>(R.id.id_treeNode_check)
        val name = binding.idTreenodeName
//            itemView.findViewById<TextView>(R.id.id_treenode_name)
//            itemView.findViewById<TextView>(R.id.size_tv)
    fun bind(item: Node, clickListener: TaskListener) {
        binding.node = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTaskBinding.inflate(layoutInflater, parent, false)
                return InnerViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mNodes[position].level
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val innerViewHolder = holder as InnerViewHolder
        innerViewHolder.bind(getItem(position)!!, clickListener)
        innerViewHolder.icon.visibility = View.VISIBLE
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
            submitList(mNodes)
        }

        innerViewHolder.icon.setOnClickListener {
            expandOrCollapse(position)
//            onTreeNodeClickListener?.onTreeItemClick(mNodes[position], position)
        }
    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     */
    private fun expandOrCollapse(position: Int) {
        val n = mNodes[position]
        n?.let {
            if (!n.isLeaf) {
                n.isExpand = !n.isExpand
                mNodes = TreeHelper.filterVisibleNode(mAllNodes)
                notifyDataSetChanged()
            }
        }
    }

    fun deleteSelectedNode() {
        val selectedIdList = getSelectedNodeListId()
        deleteNode(selectedIdList, mAllNodes)
        updateNodeList(selectedIdList, mAllNodes)
        deleteNode(selectedIdList, mNodes)
        updateNodeList(selectedIdList, mNodes)
        notifyDataSetChanged()
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
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Node, newItem: Node): Boolean {
        return oldItem == newItem
    }
}

class TaskListener(val clickListener: (task:Task)->Unit){
    fun onCLick(task:Task)= clickListener(task)
}


