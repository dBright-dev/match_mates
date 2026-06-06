package com.dbright.myapplication

import kotlin.math.abs
import kotlin.math.roundToInt

data class CompatibilityResult(
    val totalScore: Int,
    val categoryScores: Map<String, Int>
)

class CompatibilityEngine {

    fun calculateDetailedCompatibility(user1: UserProfile, user2: UserProfile): CompatibilityResult {
        val scores = mutableMapOf<String, Int>()

        // 1. Traits Similarity
        var traitScore = 0
        val commonTraits = user1.traits.keys.intersect(user2.traits.keys)
        if (commonTraits.isNotEmpty()) {
            val totalDiff = commonTraits.sumOf { trait ->
                abs((user1.traits[trait] ?: 0) - (user2.traits[trait] ?: 0))
            }
            val avgDiff = totalDiff.toFloat() / commonTraits.size
            traitScore = (100 - avgDiff).roundToInt().coerceIn(0, 100)
        } else {
            // Fallback if no traits are defined yet
            traitScore = 50 
        }
        scores["Traits"] = traitScore

        // 2. Interests Alignment
        val commonInterests = user1.interests.intersect(user2.interests.toSet()).size
        val interestScore = if (user1.interests.isNotEmpty()) {
            (commonInterests.toFloat() / user1.interests.size * 100).roundToInt().coerceIn(0, 100)
        } else 0
        scores["Interests"] = interestScore

        // 3. Goals
        val commonGoals = user1.goals.intersect(user2.goals.toSet()).size
        val goalScore = if (user1.goals.isNotEmpty()) {
            (commonGoals.toFloat() / user1.goals.size * 100).roundToInt().coerceIn(0, 100)
        } else 0
        scores["Goals"] = goalScore

        // 4. Values (Flags)
        var flagScore = 50
        val redFlagConflict = user1.redFlags.intersect(user2.skills.toSet()).size + 
                             user1.redFlags.intersect(user2.interests.toSet()).size
        flagScore -= redFlagConflict * 15
        
        val greenFlagMatch = user1.greenFlags.intersect(user2.greenFlags.toSet()).size
        flagScore += greenFlagMatch * 10
        scores["Values"] = flagScore.coerceIn(0, 100)

        val total = (traitScore * 0.4 + interestScore * 0.3 + goalScore * 0.2 + scores["Values"]!! * 0.1).roundToInt()

        return CompatibilityResult(total.coerceIn(0, 100), scores)
    }
}
