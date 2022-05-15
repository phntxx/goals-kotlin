package dev.phntxx.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.adapters.FirebaseAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase = FirebaseAdapter()
    }

    override fun onStart() {
        super.onStart()

        if (firebase.user != null) {
            val goalsActivityIntent = Intent(this, GoalsActivity::class.java)
            Log.d(TAG, "user is logged in: " + firebase.user.toString())
            startActivity(goalsActivityIntent)
        } else {
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            Log.d(TAG, "user is not logged in")
            startActivity(loginActivityIntent)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}