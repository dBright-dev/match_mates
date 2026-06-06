package com.dbright.myapplication

data class InterestGroup(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "", // Photography, Coding, Gaming, etc.
    val memberIds: List<String> = emptyList()
)
