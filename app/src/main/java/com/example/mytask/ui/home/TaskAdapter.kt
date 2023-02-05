package com.example.mytask.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytask.R
import com.example.mytask.TextItemViewHolder
import com.example.mytask.database.Task
import com.example.mytask.generated.callback.OnClickListener

class TaskAdapter : RecyclerView.Adapter<TextItemViewHolder>() {
    var data =  listOf<Task>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.taskName.toString()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.text_item_view, parent, false) as TextView
        return TextItemViewHolder(view)
    }
    class TaskListener(val clickListener:(taskId:Long)->Unit){
        fun onClick(task: Task) = clickListener(task.taskId)
    }
}