package dev.phntxx.goals

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.phntxx.goals.databinding.ActivityNewGoalBinding

class NewGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewGoalBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //binding.goBackButton.setOnClickListener {
        //    finish()
        //}
    }
}