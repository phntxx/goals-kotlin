package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.phntxx.goals.adapters.FirebaseAdapter
import dev.phntxx.goals.adapters.TaskAdapter
import dev.phntxx.goals.databinding.ActivityGoalBinding
import dev.phntxx.goals.models.GoalModel

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding
    private lateinit var firebase: FirebaseAdapter
    private lateinit var adapter: TaskAdapter
    private lateinit var goal: GoalModel
    private lateinit var goalId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goalId = intent.getStringExtra("goalId")!!

        firebase = FirebaseAdapter()
        binding = ActivityGoalBinding.inflate(layoutInflater)

        adapter = firebase.buildTaskAdapter(goalId)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        firebase.getGoal(goalId, {
            this.goal = it
            binding.toolbarLayout.title = this.goal.title

            if (this.goal.imageUUID != null) {
                Glide.with(this)
                    .load(firebase.getStorageRef(this.goal.imageUUID!!))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.goalImageView)
            }
        }, {
            finish()
        })

        binding.fab.setOnClickListener {
            val intent = Intent(this, NewTaskActivity::class.java)
            intent.putExtra("goalId", goalId)
            startActivity(intent)
        }

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.itemAnimator = null
        binding.taskRecyclerView.adapter = adapter
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

            firebase.deleteGoal(goalId, {
                finish()
            }, {
                Toast.makeText(applicationContext, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            })
        }

        return super.onOptionsItemSelected(item)
    }
}