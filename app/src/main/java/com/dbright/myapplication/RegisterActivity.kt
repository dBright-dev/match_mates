package com.dbright.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dbright.myapplication.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, ProfileSetupActivity::class.java))
                        finish()
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        Toast.makeText(this, "Registration failed: $errorMessage", Toast.LENGTH_LONG).show()
                        android.util.Log.e("RegisterActivity", "Registration failed", task.exception)
                    }
                }
            }
        }
    }
}
