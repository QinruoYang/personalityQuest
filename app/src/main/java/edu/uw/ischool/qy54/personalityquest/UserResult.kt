package edu.uw.ischool.qy54.personalityquest

import java.io.Serializable

data class UserResult(
    val name: String = "",
    val mbtiType: String = ""
) : Serializable
// user quiz result, we write to firebase, and read in Compare.
