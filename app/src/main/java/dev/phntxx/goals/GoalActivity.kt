package dev.phntxx.goals

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.databinding.ActivityGoalBinding

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var goal: DocumentSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoalBinding.inflate(layoutInflater)
        auth = Firebase.auth
        database = Firebase.firestore

        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        database
            .collection("goals")
            .document(intent.getStringExtra("goal_id")!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.get("created_by") == auth.currentUser!!.uid) {
                    goal = document
                    updateUI()
                } else {
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                finish()
            }
    }

    override fun onStart() {
        super.onStart()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun updateUI() {
        binding.toolbarLayout.title = goal.get("title").toString()
    }

    companion object {
        private const val TAG = "GoalActivity"
    }
}