package dev.phntxx.goals

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FirebaseFirestore
import com.firebase.ui.firestore.FirestoreRecyclerOptions

import dev.phntxx.goals.adapters.GoalAdapter
import dev.phntxx.goals.databinding.ActivityGoalsBinding
import dev.phntxx.goals.dialogs.GoalDialog
import dev.phntxx.goals.models.GoalModel


class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: GoalAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        database = Firebase.firestore

        binding = ActivityGoalsBinding.inflate(layoutInflater)

        val query = database
            .collection("goals")
            .whereEqualTo("uid", auth.currentUser!!.uid)

        val options = FirestoreRecyclerOptions.Builder<GoalModel>()
            .setQuery(query, GoalModel::class.java)
            .build()

        adapter = GoalAdapter(options)

        binding.goalsRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.goalsRecyclerview.adapter = adapter

        binding.newGoalButton.setOnClickListener(GoalDialog(getString(R.string.add_goal)) {
            createNewGoal(it)
        })

        binding.goalSwipeRefresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            binding.goalSwipeRefresh.isRefreshing = false
        }

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_goals, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.log_out_option) {
            auth.signOut()
            finish()
        }

        return super.onOptionsItemSelected(item)
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

    companion object {
        private const val TAG = "GoalsActivity"
    }
}
