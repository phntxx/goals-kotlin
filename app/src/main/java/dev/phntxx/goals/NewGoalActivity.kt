package dev.phntxx.goals

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.adapters.FirebaseAdapter
import dev.phntxx.goals.databinding.ActivityNewGoalBinding
import dev.phntxx.goals.models.GoalModel
import java.util.*

class NewGoalActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAdapter
    private lateinit var binding: ActivityNewGoalBinding

    private var editMode: Boolean = false
    private var imageUri: Uri? = null

    private var goal: GoalModel = GoalModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase = FirebaseAdapter()
        binding = ActivityNewGoalBinding.inflate(layoutInflater)
        editMode = intent.hasExtra("editMode")

        goal.uid = firebase.userId

        setContentView(binding.root)

        if (editMode) {
            val goalId = intent.getStringExtra("goalId")!!

            firebase.getGoal(goalId, {
                this.goal = it
                binding.goalTitle.setText(this.goal.title)

                Log.d(TAG, this.goal.imageUUID.toString())

                if (this.goal.imageUUID != null) {
                    Glide.with(this)
                        .load(firebase.getStorageRef(this.goal.imageUUID!!))
                        .into(binding.goalImageView)
                }
            })
        }

        binding.saveButton.setOnClickListener {
            binding.resetImageButton.isClickable = false
            binding.cancelButton.isClickable = false
            binding.addImageButton.isClickable = false

            val goalTitle = binding.goalTitle.text.toString()

            if (goalTitle.isEmpty()) {
                binding.goalTitle.error = getString(R.string.goal_title_length)
            } else {
                this.goal.title = binding.goalTitle.text.toString()
                if (this.imageUri != null) uploadImage(this.imageUri!!) else finalize()
            }
        }

        binding.addImageButton.setOnClickListener {
            loadImage.launch("image/*")
        }

        binding.resetImageButton.setOnClickListener {
            binding.goalImageView.setImageURI(null)
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private val loadImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.goalImageView.setImageURI(it)
    }

    private fun uploadImage(uri: Uri) {
        val uuid = UUID.randomUUID().toString()
        val ref = firebase.getStorageRef(uuid)

        ref
            .putFile(uri)
            .addOnFailureListener(defaultFailureListener)
            .addOnProgressListener {
                Toast
                    .makeText(applicationContext, R.string.uploading_image_progress, Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnSuccessListener {
                this.goal.imageUUID = uuid
                finalize()
            }
    }

    private fun finalize() {
        if (editMode) editGoal() else createNewGoal()
    }


    private fun createNewGoal() {
        firebase.createGoal(goal, {
            Log.d(TAG, "DocumentSnapshot added with ID: ${it}")
            val goalActivityIntent = Intent(this, GoalActivity::class.java)
            goalActivityIntent.putExtra("goalId", it)
            startActivity(goalActivityIntent)
        }, defaultFailureListener)
    }

    private fun editGoal() {
        val goalId = intent.getStringExtra("goalId")!!
        firebase.updateGoal(goalId, this.goal, {
            finish()
        }, defaultFailureListener)
    }

    private val defaultFailureListener = OnFailureListener {
        Log.w(TAG, "Error: ", it)
        Toast.makeText(
            applicationContext,
            getString(R.string.something_went_wrong),
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        const val TAG = "NewGoalActivity"
    }
}