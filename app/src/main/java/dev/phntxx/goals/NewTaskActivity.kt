package dev.phntxx.goals

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import dev.phntxx.goals.adapters.FirebaseAdapter
import dev.phntxx.goals.databinding.ActivityNewTaskBinding
import dev.phntxx.goals.models.Status
import dev.phntxx.goals.models.TaskModel

class NewTaskActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAdapter
    private lateinit var binding: ActivityNewTaskBinding

    private var location: List<Double>? = null

    private val getLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {

            this.location = listOf(
                it.data!!.getDoubleExtra("latitude", 0.0),
                it.data!!.getDoubleExtra("longitude", 0.0)
            )

            binding.addLocationButton.visibility = View.GONE
            binding.editLocationButton.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase = FirebaseAdapter()
        binding = ActivityNewTaskBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.fundraiser.setOnCheckedChangeListener { button, _ ->
            binding.fundraiserFormLayout.visibility = if (button.isChecked) View.VISIBLE else View.GONE
        }

        binding.location.setOnCheckedChangeListener { button, _ ->
            binding.locationFormLayout.visibility = if (button.isChecked) View.VISIBLE else View.GONE
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.addLocationButton.setOnClickListener {
            val intent = Intent(this, TaskLocationActivity::class.java)
            getLocation.launch(intent)
        }

        binding.editLocationButton.setOnClickListener {
            val intent = Intent(this, TaskLocationActivity::class.java)
            val location: DoubleArray = doubleArrayOf(
                this.location!![0],
                this.location!![1]
            )

            intent.putExtra("location", location)
            getLocation.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            if (inputValidation()) {
                val goalId: String = intent.getStringExtra("goalId")!!

                var funds: List<Double>? = null
                var location: List<Double>? = null

                if (binding.fundraiser.isChecked) {
                    funds = listOf(
                        binding.availableFunds.text.toString().toDouble(),
                        binding.neededFunds.text.toString().toDouble()
                    )
                }

                if (binding.location.isChecked) {
                    location = this.location
                }

                val task = TaskModel(
                    binding.taskName.text.toString(),
                    binding.taskDescription.text.toString(),
                    binding.critical.isChecked,
                    location,
                    funds,
                    Status.IN_PROGRESS.value
                )

                firebase.createTask(goalId, task, {
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
        val locationEnabled = binding.location.isChecked

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

        if (locationEnabled && this.location == null) {
            binding.location.error = "Location must be set if switch is set to true."
            validInput = false
        }

        return validInput
    }
}