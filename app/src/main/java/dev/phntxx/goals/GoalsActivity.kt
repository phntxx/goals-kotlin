package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.view.*
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

        val query = database
            .collection("goals")
            .whereEqualTo("uid", auth.currentUser!!.uid)

        val options = FirestoreRecyclerOptions.Builder<GoalModel>()
            .setQuery(query, GoalModel::class.java)
            .build()

        adapter = GoalAdapter(options)

        binding.newGoalButton.setOnClickListener {
            val intent = Intent(this, NewGoalActivity::class.java)
            startActivity(intent)
        }

        /**
         * I know that this is not doing anything, but I think that providing the user with
         * the illusion of control in this sense (as Firestore should update automatically)
         * makes for a better UX.
         */
        binding.goalSwipeRefresh.setOnRefreshListener {
            binding.goalSwipeRefresh.isRefreshing = false
        }

        setContentView(binding.root)

        binding.goalsRecyclerview.layoutManager = LinearLayoutManager(this)

        // HELP: Why does this line need to be here in order for all of this to work?
        binding.goalsRecyclerview.itemAnimator = null
        binding.goalsRecyclerview.adapter = adapter
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
}
