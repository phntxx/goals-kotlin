package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.adapters.GoalAdapter
import dev.phntxx.goals.databinding.ActivityGoalsBinding
import dev.phntxx.goals.models.GoalModel


class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        database = Firebase.firestore

        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = FirestoreRecyclerOptions.Builder<GoalModel>()
            .setQuery(database.collection("goals"), GoalModel::class.java)
            .build()

        adapter = GoalAdapter(options)
        binding.goalsRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.goalsRecyclerview.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()

        binding.newGoalButton.setOnClickListener {
            showAddGoalDialog()
        }

        updateUI()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_goal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.title == getString(R.string.log_out)) {
            auth.signOut()
            updateUI()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showAddGoalDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.add_goal))

        val newGoalInput = EditText(this)
        newGoalInput.inputType = InputType.TYPE_CLASS_TEXT
        alertDialogBuilder.setView(newGoalInput)

        alertDialogBuilder.setPositiveButton(android.R.string.yes) { _, _ ->
            val newGoalTitle = newGoalInput.text.toString()
            createNewGoal(newGoalTitle)
        }

        alertDialogBuilder.setNegativeButton(android.R.string.no) { _, _ ->
            return@setNegativeButton
        }

        alertDialogBuilder.show()
    }

    private fun createNewGoal(title: String) {

        val goal = GoalModel(title, auth.currentUser!!.uid)

        database.collection("goals")
            .add(goal)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                val goalActivityIntent = Intent(this, GoalActivity::class.java)
                goalActivityIntent.putExtra("goalId", documentReference.id)
                startActivity(goalActivityIntent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun updateUI() {
        if (auth.currentUser == null) {
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            Log.d(TAG, "user is not logged in")
            startActivity(loginActivityIntent)
        }
    }

    companion object {
        private const val TAG = "GoalsActivity"
    }
}
