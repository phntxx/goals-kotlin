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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dev.phntxx.goals.databinding.ActivityNewGoalBinding
import dev.phntxx.goals.models.GoalModel
import java.util.*

class NewGoalActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var binding: ActivityNewGoalBinding

    private var editMode: Boolean = false
    private var imageUri: Uri? = null

    private var goal: GoalModel = GoalModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        database = Firebase.firestore
        storage = FirebaseStorage.getInstance()

        binding = ActivityNewGoalBinding.inflate(layoutInflater)
        editMode = intent.hasExtra("editMode")

        goal.uid = auth.currentUser!!.uid

        setContentView(binding.root)

        if (editMode) {
            val goalId = intent.getStringExtra("goalId")!!
            this.getGoal(goalId)
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
        val ref = storage.getReference("images").child(uuid)

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

    private fun getGoal(goalId: String) {
        database
            .collection("goals")
            .document(goalId)
            .get()
            .addOnSuccessListener {
                this.goal = GoalModel.fromDocumentSnapshot(it)
                binding.goalTitle.setText(this.goal.title)

                Log.d(TAG, this.goal.imageUUID.toString())

                if (this.goal.imageUUID != null) {
                    Glide.with(this).asDrawable()
                        .load(storage.getReference("images").child(this.goal.imageUUID!!))
                        .into(binding.goalImageView)
                }
            }
    }

    private fun createNewGoal() {
        database
            .collection("goals")
            .add(goal)
            .addOnFailureListener(defaultFailureListener)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${it.id}")
                val goalActivityIntent = Intent(this, GoalActivity::class.java)
                goalActivityIntent.putExtra("goalId", it.id)
                startActivity(goalActivityIntent)
            }
    }

    private fun editGoal() {
        val goalId = intent.getStringExtra("goalId")!!

        database
            .collection("goals")
            .document(goalId)
            .update(this.goal.toMap())
            .addOnFailureListener(defaultFailureListener)
            .addOnSuccessListener {
                Log.d(TAG, "Updated document with ID: $goalId")
                finish()
            }
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