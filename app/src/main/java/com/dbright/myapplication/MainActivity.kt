package com.dbright.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

        findViewById<View>(R.id.btnViewDetails).setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        
        val homeSection = findViewById<View>(R.id.llHomeContent)
        val exploreSection = findViewById<View>(R.id.rvEvents)
        val messagesSection = findViewById<View>(R.id.layoutGroups)

        bottomNav.setOnItemSelectedListener { item ->
            // Reset visibility
            homeSection.visibility = View.GONE
            exploreSection.visibility = View.GONE
            messagesSection.visibility = View.GONE
            
            when (item.itemId) {
                R.id.navigation_home -> {
                    homeSection.visibility = View.VISIBLE
                    true
                }
                R.id.navigation_explore -> {
                    exploreSection.visibility = View.VISIBLE
                    fetchEvents()
                    true
                }
                R.id.navigation_messages -> {
                    messagesSection.visibility = View.VISIBLE
                    setupGroupsAndChats()
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

    private fun fetchEvents() {
        val rvEvents = findViewById<RecyclerView>(R.id.rvEvents)
        rvEvents.layoutManager = LinearLayoutManager(this)
        db.collection("events").get().addOnSuccessListener { snapshot ->
            val events = snapshot.toObjects(Event::class.java)
            rvEvents.adapter = EventsAdapter(events) { event ->
                Toast.makeText(this, "Interested in ${event.title}", Toast.LENGTH_SHORT).show()
            }
            rvEvents.visibility = View.VISIBLE
        }
    }

    private fun setupGroupsAndChats() {
        val layoutGroups = findViewById<View>(R.id.layoutGroups)
        val tabLayout = layoutGroups.findViewById<com.google.android.material.tabs.TabLayout>(R.id.groupsTabLayout)
        val rvGroups = layoutGroups.findViewById<RecyclerView>(R.id.rvGroupsList)
        val rvChats = layoutGroups.findViewById<RecyclerView>(R.id.rvChatsList)

        rvGroups.layoutManager = LinearLayoutManager(this)
        rvChats.layoutManager = LinearLayoutManager(this)

        db.collection("groups").get().addOnSuccessListener { snapshot ->
            val groups = snapshot.toObjects(InterestGroup::class.java)
            rvGroups.adapter = GroupsAdapter(groups) { group ->
                Toast.makeText(this, "Joined ${group.name}", Toast.LENGTH_SHORT).show()
            }
        }

        tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                if (tab?.position == 0) {
                    rvGroups.visibility = View.VISIBLE
                    rvChats.visibility = View.GONE
                } else {
                    rvGroups.visibility = View.GONE
                    rvChats.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
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
