package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dev.phntxx.goals.databinding.ActivityGoalBinding
import dev.phntxx.goals.models.GoalModel

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var goal: GoalModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoalBinding.inflate(layoutInflater)
        auth = Firebase.auth
        database = Firebase.firestore
        storage = FirebaseStorage.getInstance()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        database
            .collection("goals")
            .document(intent.getStringExtra("goalId")!!)
            .get()
            .addOnSuccessListener { document ->
                this.goal = GoalModel.fromDocumentSnapshot(document)
                binding.toolbarLayout.title = this.goal.title
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                finish()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_goal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val goalId: String = intent.getStringExtra("goalId")!!

        if (item.itemId == R.id.edit_goal_option) {
            val intent = Intent(this, NewGoalActivity::class.java)
            intent.putExtra("editMode", true)
            intent.putExtra("goalId", goalId)
            startActivity(intent)
        }

        if (item.itemId == R.id.delete_goal_option) {

            database
                .collection("goals")
                .document(goalId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Deleted goal")
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error removing goal", e)

                }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "GoalActivity"
    }
}