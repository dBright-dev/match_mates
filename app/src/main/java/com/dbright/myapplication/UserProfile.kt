package com.dbright.myapplication

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val occupation: String = "",
    val bio: String = "",
    val personalityType: String = "",
    val goals: List<String> = emptyList(),
    val traits: Map<String, Int> = emptyMap(),
    val skills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val redFlags: List<String> = emptyList(),
    val greenFlags: List<String> = emptyList(),
    val profileImageUrl: String = "",
    
    // 🧠 Personality & Behavior
    val communicationStyle: String = "",
    val decisionMaking: String = "",
    val conflictResolution: String = "",
    val energyLevel: Int = 5,
    val workLifeBalance: Int = 5,
    
    // 🎯 Interests & Lifestyle
    val favoriteGenres: List<String> = emptyList(),
    val preferredActivities: List<String> = emptyList(),
    val travelHabits: String = "",
    val fitnessLevel: String = "",
    val dietaryPreference: String = "",
    
    // 💬 Social & Emotional Traits
    val empathyLevel: Int = 5,
    val humorType: String = "",
    val emotionalOpenness: String = "",
    val trustPace: String = "",
    val primaryValues: List<String> = emptyList(),
    
    // 🚦 Flags & Boundaries
    val dealBreakers: List<String> = emptyList(),
    val boundaries: String = "",
    val differenceTolerance: String = "",
    
    // 💡 Compatibility Metrics
    val learningStyle: String = "",
    val motivationDrivers: List<String> = emptyList(),
    val interactionFrequency: String = "",
    
    // 🎨 Optional Enhancements
    val moodColor: String = "",
    val favoriteQuote: String = "",
    val entertainmentPrefs: List<String> = emptyList(),
    
    // 🏅 Gamification
    val badges: List<String> = emptyList(),
    val points: Int = 0
)
