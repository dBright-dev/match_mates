package com.dbright.myapplication

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

object GamificationManager {

    private val db = FirebaseFirestore.getInstance()

    fun awardBadge(userId: String, badgeName: String, onComplete: (Boolean) -> Unit = {}) {
        val userRef = db.collection("users").document(userId)
        
        userRef.get().addOnSuccessListener { doc ->
            val currentBadges = doc.get("badges") as? List<*> ?: emptyList<String>()
            if (!currentBadges.contains(badgeName)) {
                userRef.update(
                    "badges", FieldValue.arrayUnion(badgeName),
                    "points", FieldValue.increment(50)
                ).addOnCompleteListener { onComplete(it.isSuccessful) }
            } else {
                onComplete(false)
            }
        }
    }

    fun addPoints(userId: String, amount: Long) {
        db.collection("users").document(userId)
            .update("points", FieldValue.increment(amount))
    }
}
