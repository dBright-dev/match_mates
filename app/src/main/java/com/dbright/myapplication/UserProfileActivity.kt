package com.dbright.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val toolbar = findViewById<MaterialToolbar>(R.id.profileToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, ProfileSetupActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        loadUserData()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            try {
                val user = doc.toObject<UserProfile>()
                if (user != null) {
                    displayProfile(user)
                }
            } catch (e: Exception) {
                Log.e("UserProfile", "Error parsing profile", e)
                Toast.makeText(this, "Error parsing profile", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("UserProfile", "Failed to load profile", it)
            Toast.makeText(this, "Failed to load profile: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayProfile(user: UserProfile) {
        findViewById<TextView>(R.id.tvProfileName).text = user.name
        findViewById<TextView>(R.id.tvProfileOccupation).text = user.occupation
        findViewById<TextView>(R.id.tvQuote).text = if (user.favoriteQuote.isNotEmpty()) "\"${user.favoriteQuote}\"" else ""
        
        findViewById<TextView>(R.id.tvCommStyle).text = "Communication: ${user.communicationStyle.ifEmpty { "Not set" }}"
        findViewById<TextView>(R.id.tvEnergyLevel).text = "Energy Level: ${user.energyLevel}/10"
        findViewById<TextView>(R.id.tvDietary).text = "Dietary: ${user.dietaryPreference.ifEmpty { "Not set" }}"
        findViewById<TextView>(R.id.tvPoints).text = "Points: ${user.points}"

        val cgBadges = findViewById<ChipGroup>(R.id.cgBadges)
        cgBadges.removeAllViews()
        user.badges.forEach { badge ->
            cgBadges.addView(Chip(this).apply { 
                text = badge 
                setChipIconResource(android.R.drawable.btn_star_big_on)
            })
        }

        val ivProfile = findViewById<ImageView>(R.id.ivProfileLarge)
        if (user.profileImageUrl.isNotEmpty()) {
            Glide.with(this).load(user.profileImageUrl).into(ivProfile)
        }

        val cgActivities = findViewById<ChipGroup>(R.id.cgActivities)
        cgActivities.removeAllViews()
        user.preferredActivities.forEach { activity ->
            cgActivities.addView(Chip(this).apply { text = activity })
        }

        val cgGreenFlags = findViewById<ChipGroup>(R.id.cgGreenFlags)
        cgGreenFlags.removeAllViews()
        user.greenFlags.forEach { flag ->
            cgGreenFlags.addView(Chip(this).apply { 
                text = flag
                setChipBackgroundColorResource(android.R.color.holo_green_light)
                setTextColor(Color.WHITE)
            })
        }

        val cgRedFlags = findViewById<ChipGroup>(R.id.cgRedFlags)
        cgRedFlags.removeAllViews()
        user.redFlags.forEach { flag ->
            cgRedFlags.addView(Chip(this).apply {
                text = flag
                setChipBackgroundColorResource(android.R.color.holo_red_light)
                setTextColor(Color.WHITE)
            })
        }
    }
}
