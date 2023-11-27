package edu.uw.ischool.qy54.personalityquest

data class UserResult(
    val name: String = "",
    val mbtiType: String = "",
    val percentages: Map<String, Int> = emptyMap()
)
// user quiz result, we write to firebase, and read in Compare.
