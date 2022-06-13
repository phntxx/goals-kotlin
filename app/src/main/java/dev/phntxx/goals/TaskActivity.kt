package dev.phntxx.goals

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.phntxx.goals.adapters.FirebaseAdapter
import dev.phntxx.goals.databinding.ActivityTaskBinding
import dev.phntxx.goals.models.Status
import dev.phntxx.goals.models.TaskModel

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding

    private lateinit var firebase: FirebaseAdapter
    private lateinit var task: TaskModel

    private lateinit var goalId: String
    private lateinit var taskId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goalId = intent.getStringExtra("goalId")!!
        taskId = intent.getStringExtra("taskId")!!

        firebase = FirebaseAdapter()
        binding = ActivityTaskBinding.inflate(layoutInflater)

        firebase.getTask(goalId, taskId, {
            this.task = it

            if (task.title != null) binding.taskName.text = task.title
            if (task.description != null) binding.taskDescription.text = task.description

            if (task.critical == true) {
                binding.taskCritical.visibility = View.VISIBLE
                binding.taskCritical.setText(R.string.critical_task)
            }

            if (task.funds != null) {
                binding.taskFundsLayout.visibility = View.VISIBLE
                binding.fundsText.text = getString(
                    R.string.funds_text,
                    task.funds!![0].toString(),
                    task.funds!![1].toString()
                )
                binding.fundsBar.progress = ((task.funds!![0] / task.funds!![1]) * 100).toInt()
            }

            if (task.location != null) {
                binding.taskMapLayout.visibility = View.VISIBLE
            }

            setButtonsEnabled(Status.fromValue(task.status))

            binding.completedButton.setOnClickListener {
                firebase.updateTaskStatus(goalId, taskId, Status.COMPLETED.value, {
                    this.setButtonsEnabled(Status.COMPLETED)
                })
            }

            binding.inProgressButton.setOnClickListener {
                firebase.updateTaskStatus(goalId, taskId, Status.IN_PROGRESS.value, {
                    this.setButtonsEnabled(Status.IN_PROGRESS)
                })
            }

            binding.failedButton.setOnClickListener {
                firebase.updateTaskStatus(goalId, taskId, Status.FAILED.value, {
                    this.setButtonsEnabled(Status.FAILED)
                })
            }

        }, {
            finish()
        })

        setContentView(binding.root)
    }

    private fun setButtonsEnabled(status: Status) {
        binding.completedButton.isEnabled = status != Status.COMPLETED
        binding.inProgressButton.isEnabled = status != Status.IN_PROGRESS
        binding.failedButton.isEnabled = status != Status.FAILED
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_task, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.delete_task_option) {
            firebase.deleteTask(goalId, taskId, {
                finish()
            })
        }

        return super.onOptionsItemSelected(item)
    }
}