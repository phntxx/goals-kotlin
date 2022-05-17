package dev.phntxx.goals.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dev.phntxx.goals.GoalActivity
import dev.phntxx.goals.databinding.GoalCardBinding
import dev.phntxx.goals.models.GoalModel


class GoalAdapter(options: FirebaseRecyclerOptions<GoalModel>) : FirebaseRecyclerAdapter<GoalModel, GoalAdapter.MainHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = GoalCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int, model: GoalModel) {
        holder.bind(model, position)
    }

    inner class MainHolder(private val binding : GoalCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: GoalModel, position: Int) {
            binding.goalTitle.text = goal.title

            binding.root.setOnClickListener { view ->
                val goalId = snapshots.getSnapshot(position).key
                val goalActivityIntent = Intent(view.context, GoalActivity::class.java)
                goalActivityIntent.putExtra("goalId", goalId)
                view.context.startActivity(goalActivityIntent)
            }
        }
    }
}