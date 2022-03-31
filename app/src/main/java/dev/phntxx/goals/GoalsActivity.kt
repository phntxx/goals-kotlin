package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.databinding.ActivityGoalsBinding

class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoalsBinding.inflate(layoutInflater)
        auth = Firebase.auth

        setContentView(binding.root)

        binding.logOutButton.setOnClickListener {
            auth.signOut()
            updateUI()
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun updateUI() {
        if (auth.currentUser == null) {
            var loginActivityIntent = Intent(this, LoginActivity::class.java)
            Log.d(TAG, "user is not logged in")
            startActivity(loginActivityIntent)
        } else {
            binding.userTextview.text = auth.currentUser!!.email
        }
    }

    companion object {
        private const val TAG = "GoalsActivity"
    }
}