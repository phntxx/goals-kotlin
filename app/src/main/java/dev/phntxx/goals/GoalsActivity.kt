package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.phntxx.goals.adapters.FirebaseAdapter
import dev.phntxx.goals.adapters.GoalAdapter
import dev.phntxx.goals.databinding.ActivityGoalsBinding


class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private lateinit var firebase: FirebaseAdapter
    private lateinit var adapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase = FirebaseAdapter()
        adapter = firebase.buildGoalAdapter()
        binding = ActivityGoalsBinding.inflate(layoutInflater)

        binding.newGoalButton.setOnClickListener {
            val intent = Intent(this, NewGoalActivity::class.java)
            startActivity(intent)
        }

        binding.goalSwipeRefresh.setOnRefreshListener {
            binding.goalSwipeRefresh.isRefreshing = false
        }

        setContentView(binding.root)

        binding.goalsRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.goalsRecyclerview.itemAnimator = null
        binding.goalsRecyclerview.adapter = adapter
        adapter
            .buildItemTouchHelper(this)
            .attachToRecyclerView(binding.goalsRecyclerview)
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
            firebase.signOut()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
