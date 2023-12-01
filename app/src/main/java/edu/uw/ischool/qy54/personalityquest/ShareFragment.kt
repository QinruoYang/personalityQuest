package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast

class ShareFragment : Fragment() {
    private lateinit var editTextUserName: EditText
    private lateinit var buttonShareResult: Button
    private var mbtiResult: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.share_fragment, container, false)
        editTextUserName = view.findViewById(R.id.editTextUserName)
        buttonShareResult = view.findViewById(R.id.buttonShareResult)

        // get MBTI result passed from MyResult fragment
        arguments?.let {
            mbtiResult = it.getString("mbtiResult")
        }

        buttonShareResult.setOnClickListener {
            val userName = editTextUserName.text.toString()
            shareResultToFirebase(userName, mbtiResult)
        }

        return view
    }

    private fun shareResultToFirebase(userName: String, userResult: String?) {

        if (userName.isBlank() || userResult.isNullOrEmpty()) {
            Toast.makeText(context, "Please enter your name and make sure you have a result.", Toast.LENGTH_LONG).show()
            return
        }

        // Get a reference to reatimedb
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("results")

        val resultId = ref.push().key 

        val data = mapOf(
            "name" to userName,
            "mbtiType" to userResult
        )

        // Write to db
        resultId?.let {
            ref.child(it).setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(context, "Result shared successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to share result. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}