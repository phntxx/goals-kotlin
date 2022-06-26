package dev.phntxx.goals.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dev.phntxx.goals.R
import dev.phntxx.goals.TaskActivity
import dev.phntxx.goals.databinding.TaskCardBinding
import dev.phntxx.goals.models.TaskModel

class TaskAdapter(options: FirebaseRecyclerOptions<TaskModel>, private val goalId: String): FirebaseRecyclerAdapter<TaskModel, TaskAdapter.MainHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = TaskCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding, goalId)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int, model: TaskModel) {
        holder.bind(model, position)
    }

    fun removeItem(position: Int) {
        snapshots.getSnapshot(position).ref.removeValue()
    }

    inner class MainHolder(private val binding : TaskCardBinding, private val goalId: String) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(task: TaskModel, position: Int) {

            val informationText = binding.root.resources.getStringArray(R.array.task_status_array)[task.status]

            if (task.critical == true) {
                val criticalText = binding.root.resources.getString(R.string.critical_task)
                binding.taskInformation.text = "$criticalText • $informationText"
            } else {
                binding.taskInformation.text = informationText
            }

            if (task.title != null) {
                binding.taskName.visibility = View.VISIBLE
                binding.taskName.text = task.title
            }

            if (task.description != null) {
                binding.taskDescription.visibility = View.VISIBLE
                binding.taskDescription.text = task.description
            }

            binding.root.setOnClickListener {
                val taskId = snapshots.getSnapshot(position).key
                val goalActivityIntent = Intent(it.context, TaskActivity::class.java)
                goalActivityIntent.putExtra("taskId", taskId)
                goalActivityIntent.putExtra("goalId", goalId)
                it.context.startActivity(goalActivityIntent)
            }
        }
    }
}