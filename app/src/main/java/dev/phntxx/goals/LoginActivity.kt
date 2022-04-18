package dev.phntxx.goals

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.phntxx.goals.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

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

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser, true)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            updateUI(auth.currentUser, false)
        } else {
            updateUI(null, false)
        }
    }

    private fun updateUI(account: FirebaseUser?, onStart: Boolean){
        if (account != null) {
            var goalsActivityIntent = Intent(this, GoalsActivity::class.java)
            startActivity(goalsActivityIntent)
        } else {
            val errorMessage = Toast.makeText(
                applicationContext,
                getString(R.string.something_went_wrong),
                Toast.LENGTH_LONG
            )

            if (!onStart) errorMessage.show()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}