package dev.phntxx.goals.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
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

    fun buildItemTouchHelper (context: Context): ItemTouchHelper {

        val alertDialogBuilder = AlertDialog.Builder(context)
            .setTitle("Are you sure you want to delete this goal?")
            .setMessage("This action cannot be undone.")
            .setCancelable(true)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                    snapshots.getSnapshot(viewHolder.bindingAdapterPosition).ref.removeValue()
                }

                val alert = alertDialogBuilder.create()
                alert.show()
            }
        })
    }

    inner class MainHolder(private val binding : GoalCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: GoalModel, position: Int) {
            binding.goalTitle.text = goal.title

            binding.root.setOnClickListener {
                val goalId = snapshots.getSnapshot(position).key
                val goalActivityIntent = Intent(it.context, GoalActivity::class.java)
                goalActivityIntent.putExtra("goalId", goalId)
                it.context.startActivity(goalActivityIntent)
            }
        }
    }
}