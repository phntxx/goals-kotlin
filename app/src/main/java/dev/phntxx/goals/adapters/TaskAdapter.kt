package dev.phntxx.goals.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dev.phntxx.goals.R
import dev.phntxx.goals.databinding.TaskCardBinding
import dev.phntxx.goals.models.TaskModel

class TaskAdapter(options: FirebaseRecyclerOptions<TaskModel>): FirebaseRecyclerAdapter<TaskModel, TaskAdapter.MainHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = TaskCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int, model: TaskModel) {
        Log.d("TESTING", model.title ?: "fuck...")
        holder.bind(model, position)
    }

    inner class MainHolder(private val binding : TaskCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskModel, position: Int) {

            if (task.critical == true) {
                binding.taskCritical.setText(R.string.critical_task)
            }

            binding.taskName.text = task.title
            binding.taskDescription.text = task.description
        }
    }
}