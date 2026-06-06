package com.dbright.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var rvMatches: RecyclerView
    private lateinit var tvGreeting: TextView
    private lateinit var tvOverallScore: TextView
    private var currentUserProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        setupViews()
        setupBottomNav()
        fetchCurrentUserAndMatches()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
    }

    private fun setupViews() {
        rvMatches = findViewById(R.id.rvMatches)
        tvGreeting = findViewById(R.id.tvGreeting)
        tvOverallScore = findViewById(R.id.tvOverallScore)

        rvMatches.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on Home/Dashboard
                    true
                }
                R.id.navigation_explore -> {
                    // Could navigate to a separate Explore fragment or view
                    true
                }
                R.id.navigation_messages -> {
                    // Navigate to Chats
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun fetchCurrentUserAndMatches() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            currentUserProfile = doc.toObject<UserProfile>()
            currentUserProfile?.let { profile ->
                tvGreeting.text = "Welcome Back, ${profile.name},"
                fetchAllUsers()
            } ?: run {
                startActivity(Intent(this, ProfileSetupActivity::class.java))
                finish()
            }
        }
    }

    private fun fetchAllUsers() {
        val uid = auth.currentUser?.uid
        db.collection("users").get().addOnSuccessListener { snapshot ->
            val allUsers = snapshot.toObjects(UserProfile::class.java)
            val matches = allUsers.filter { it.id != uid }
            
            if (currentUserProfile != null) {
                rvMatches.adapter = MatchesAdapter(currentUserProfile!!, matches)
                
                // Calculate an average or top score for the gauge
                if (matches.isNotEmpty()) {
                    val engine = CompatibilityEngine()
                    val topScore = matches.maxOf { engine.calculateDetailedCompatibility(currentUserProfile!!, it).totalScore }
                    tvOverallScore.text = "${topScore}%"
                }
            }
        }
    }
}
