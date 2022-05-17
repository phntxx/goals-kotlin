package dev.phntxx.goals

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import dev.phntxx.goals.adapters.FirebaseAdapter
import dev.phntxx.goals.databinding.ActivityGoalsBinding
import dev.phntxx.goals.databinding.ActivityNewTaskBinding
import dev.phntxx.goals.models.TaskModel

class NewTaskActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAdapter
    private lateinit var binding: ActivityNewTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase = FirebaseAdapter()
        binding = ActivityNewTaskBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.fundraiser.setOnCheckedChangeListener { button, _ ->
            binding.fundraiserFormLayout.visibility = if (button.isChecked) View.VISIBLE else View.GONE
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            if (inputValidation()) {
                val goalId: String = intent.getStringExtra("goalId")!!

                var funds: DoubleArray? = null
                if (binding.fundraiser.isChecked) {
                    funds = doubleArrayOf(
                        binding.availableFunds.text.toString().toDouble(),
                        binding.neededFunds.text.toString().toDouble()
                    )
                }

                val task = TaskModel(
                    binding.taskName.text.toString(),
                    binding.taskDescription.text.toString(),
                    binding.critical.isChecked,
                    null, // location not implemented yet
                    funds,
                )

                firebase.addTasktoGoal(goalId, task, {
                    finish()
                }, {
                    Toast.makeText(
                        applicationContext,
                        R.string.something_went_wrong,
                        Toast.LENGTH_LONG
                    ).show()
                })
            }
        }
    }

    private fun inputValidation(): Boolean {

        var validInput = true
        val taskName = binding.taskName.text

        val fundRaiserEnabled = binding.fundraiser.isChecked

        if (taskName.isEmpty()) {
            binding.taskName.error = "Task name must not be empty"
            validInput = false
        }

        if (fundRaiserEnabled) {
            val fundraiserMinimum = binding.availableFunds.text.toString().toDouble()
            val fundraiserMaximum = binding.neededFunds.text.toString().toDouble()

            if (fundraiserMinimum >= fundraiserMaximum) {
                binding.availableFunds.error = "Available funds must not be equal to or exceed needed funds."
                validInput = false
            }
        }

        return validInput
    }
}