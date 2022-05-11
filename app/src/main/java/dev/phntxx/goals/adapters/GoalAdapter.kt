package dev.phntxx.goals.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import dev.phntxx.goals.GoalActivity
import dev.phntxx.goals.databinding.GoalCardBinding
import dev.phntxx.goals.models.GoalModel


class GoalAdapter(options: FirestoreRecyclerOptions<GoalModel>) : FirestoreRecyclerAdapter<GoalModel, GoalAdapter.MainHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = GoalCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int, model: GoalModel) {
        holder.bind(model, position)
    }

    override fun onError(e: FirebaseFirestoreException) {
        Log.e(TAG, e.toString())
    }

    inner class MainHolder(private val binding : GoalCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: GoalModel, position: Int) {
            binding.goalTitle.text = goal.title

            binding.root.setOnClickListener { view ->
                val goalActivityIntent = Intent(view.context, GoalActivity::class.java)
                goalActivityIntent.putExtra("goalId", snapshots.getSnapshot(position).id)
                view.context.startActivity(goalActivityIntent)
            }
        }
    }

    companion object {
        private const val TAG = "GoalAdapter"
    }
}