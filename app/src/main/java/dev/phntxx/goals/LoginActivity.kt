package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
            ))
            .build()

        binding.signInButton.setOnClickListener {
            signInLauncher.launch(signInIntent)
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
        if (it.resultCode == RESULT_OK) {
            val goalsActivityIntent = Intent(this, GoalsActivity::class.java)
            startActivity(goalsActivityIntent)
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.something_went_wrong),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}