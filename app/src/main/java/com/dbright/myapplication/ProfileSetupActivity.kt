package com.dbright.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dbright.myapplication.databinding.ActivityProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        loadExistingProfile()

        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadExistingProfile() {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("ProfileSetup", "No user logged in")
            return
        }
        Log.d("ProfileSetup", "Loading profile for: $userId")
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                Log.d("ProfileSetup", "Document data: ${document.data}")
                try {
                    val profile = document.toObject(UserProfile::class.java)
                    profile?.let { populateUI(it) }
                } catch (e: Exception) {
                    Log.e("ProfileSetup", "Error parsing profile", e)
                    Toast.makeText(this, "Error parsing profile data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("ProfileSetup", "No existing profile found for this user")
            }
        }.addOnFailureListener { exception ->
            Log.e("ProfileSetup", "Error fetching profile", exception)
            Toast.makeText(this, "Failed to load data: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun populateUI(profile: UserProfile) {
        binding.etName.setText(profile.name)
        binding.etOccupation.setText(profile.occupation)
        
        // Personality
        when (profile.communicationStyle) {
            "Direct" -> binding.rbDirect.isChecked = true
            "Empathetic" -> binding.rbEmpathetic.isChecked = true
            "Analytical" -> binding.rbAnalytical.isChecked = true
        }
        binding.etDecisionMaking.setText(profile.decisionMaking)
        binding.sliderEnergy.value = profile.energyLevel.toFloat()

        // Lifestyle
        binding.etGenres.setText(profile.favoriteGenres.joinToString(", "))
        binding.cbOutdoors.isChecked = profile.preferredActivities.contains("Outdoors")
        binding.cbTech.isChecked = profile.preferredActivities.contains("Tech Tinkering")
        binding.cbCreative.isChecked = profile.preferredActivities.contains("Creative Arts")
        binding.etDiet.setText(profile.dietaryPreference)

        // Flags
        binding.etGreenFlags.setText(profile.greenFlags.joinToString(", "))
        binding.etRedFlags.setText(profile.redFlags.joinToString(", "))

        // Optional
        binding.etQuote.setText(profile.favoriteQuote)
    }

    private fun saveProfile() {
        Log.d("ProfileSetup", "Save button clicked")
        val name = binding.etName.text.toString()
        val occupation = binding.etOccupation.text.toString()
        
        if (name.isEmpty() || occupation.isEmpty()) {
            Toast.makeText(this, "Please fill in Name and Occupation", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        
        val communicationId = binding.rgCommunication.checkedRadioButtonId
        val communicationStyle = if (communicationId != -1) findViewById<RadioButton>(communicationId).text.toString() else ""
        
        val preferredActivities = mutableListOf<String>()
        if (binding.cbOutdoors.isChecked) preferredActivities.add("Outdoors")
        if (binding.cbTech.isChecked) preferredActivities.add("Tech Tinkering")
        if (binding.cbCreative.isChecked) preferredActivities.add("Creative Arts")

        val profile = UserProfile(
            id = userId,
            name = name,
            occupation = occupation,
            communicationStyle = communicationStyle,
            decisionMaking = binding.etDecisionMaking.text.toString(),
            energyLevel = binding.sliderEnergy.value.toInt(),
            favoriteGenres = binding.etGenres.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            preferredActivities = preferredActivities,
            dietaryPreference = binding.etDiet.text.toString(),
            greenFlags = binding.etGreenFlags.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            redFlags = binding.etRedFlags.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            favoriteQuote = binding.etQuote.text.toString(),
            profileImageUrl = "https://images.unsplash.com/photo-1511367461989-f85a21fda167?q=80&w=200&auto=format&fit=crop"
        )

        db.collection("users").document(userId).set(profile).addOnSuccessListener {
            Log.d("ProfileSetup", "Profile saved successfully")
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            Log.e("ProfileSetup", "Error saving profile", it)
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
