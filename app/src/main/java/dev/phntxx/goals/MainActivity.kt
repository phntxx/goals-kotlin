package dev.phntxx.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            var goalsActivityIntent = Intent(this, GoalsActivity::class.java)
            Log.d(TAG, "user is logged in: " + auth.currentUser.toString())
            startActivity(goalsActivityIntent)
        } else {
            var loginActivityIntent = Intent(this, LoginActivity::class.java)
            Log.d(TAG, "user is not logged in")
            startActivity(loginActivityIntent)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}