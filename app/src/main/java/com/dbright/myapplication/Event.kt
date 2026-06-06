package com.dbright.myapplication

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val creatorId: String = "",
    val location: String = "",
    val type: String = "" // Meetup, Study Session, etc.
)
