package dev.phntxx.goals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.databinding.ActivityGoalBinding
import dev.phntxx.goals.models.GoalModel

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var goal: GoalModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoalBinding.inflate(layoutInflater)
        auth = Firebase.auth
        database = Firebase.firestore

        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        database
            .collection("goals")
            .document(intent.getStringExtra("goalId")!!)
            .get()
            .addOnSuccessListener { document ->
                this.goal = GoalModel.fromDocumentSnapshot(document)
                if (this.goal.uid == auth.currentUser!!.uid) updateUI() else finish()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_goal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.edit_goal_option) {
            showEditGoalDialog()
        }

        if (item.itemId == R.id.delete_goal_option) {

            val goalId: String = intent.getStringExtra("goalId")!!
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
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun editGoal(title: String) {

        val goalId = intent.getStringExtra("goalId")!!

        database
            .collection("goals")
            .document(goalId)
            .update("title", title)
            .addOnSuccessListener {
                Log.d(TAG, "Updated document with ID: ${goalId}")
                this.goal.title = title
                updateUI()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun showEditGoalDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.edit_goal))

        val newGoalNameInput = EditText(this)
        newGoalNameInput.inputType = InputType.TYPE_CLASS_TEXT
        alertDialogBuilder.setView(newGoalNameInput)

        alertDialogBuilder.setPositiveButton(android.R.string.yes) { _, _ ->
            val newGoalTitle = newGoalNameInput.text.toString()
            editGoal(newGoalTitle)
        }

        alertDialogBuilder.setNegativeButton(android.R.string.no) { _, _ ->
            return@setNegativeButton
        }

        alertDialogBuilder.show()
    }

    private fun updateUI() {
        binding.toolbarLayout.title = goal.title
    }

    companion object {
        private const val TAG = "GoalActivity"
    }
}