package dev.phntxx.goals

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import dev.phntxx.goals.adapters.FirebaseAdapter
import dev.phntxx.goals.databinding.ActivityNewGoalBinding
import dev.phntxx.goals.models.GoalModel

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

                if (this.goal.imageUUID != null) {

                    Glide.with(this)
                        .load(firebase.getStorageRef(this.goal.imageUUID!!))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
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
                if (editMode) editGoal() else createGoal()
            }
        }

        binding.addImageButton.setOnClickListener {
            loadImage.launch("image/*")
        }

        binding.resetImageButton.setOnClickListener {
            binding.goalImageView.setImageDrawable(null)
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun createGoal() {
        // ImageURI exists => Drawable exists
        // Drawable does not exist => No change

        // imageURI exists => Image needs to be uploaded
        if (this.imageUri != null) {
            uploadImage(this.imageUri!!)
        } else {
            uploadGoal()
        }
    }

    private fun editGoal() {
        val drawableExists = (this.binding.goalImageView.drawable != null)
        val goalUUIDExists = (this.goal.imageUUID != null)
        val imageURIExists = (this.imageUri != null)

        // Drawable exists & ImageURI does not exist => Image needs to be kept => No change

        // Drawable exists & ImageURI exists => Image needs to be replaced
        if (goalUUIDExists && imageURIExists) {
            firebase.updateGoalImage(this.goal.imageUUID!!, this.imageUri!!, {}, defaultProgressListener)
        }

        // Drawable does not exist => Image needs to be removed
        if (!drawableExists && goalUUIDExists) {
            firebase.deleteGoalImage(this.goal.imageUUID!!, {
                this.goal.imageUUID = null
            })
        }

        val goalId = intent.getStringExtra("goalId")!!

        firebase.updateGoal(goalId, this.goal, {
            finish()
        }, defaultFailureListener)
    }

    private fun uploadGoal () {
        firebase.createGoal(goal, {
            Log.d(TAG, "DocumentSnapshot added with ID: $it")
            val goalActivityIntent = Intent(this, GoalActivity::class.java)
            goalActivityIntent.putExtra("goalId", it)
            startActivity(goalActivityIntent)
        }, defaultFailureListener)
    }

    private val loadImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.goalImageView.setImageURI(it)
    }

    private fun uploadImage(uri: Uri) {
        firebase.uploadGoalImage(uri, {
            this.goal.imageUUID = it
            uploadGoal()
        }, defaultProgressListener, defaultFailureListener)
    }

    private val defaultProgressListener = OnProgressListener<UploadTask.TaskSnapshot> {
        Toast
            .makeText(applicationContext, R.string.uploading_image_progress, Toast.LENGTH_SHORT)
            .show()
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