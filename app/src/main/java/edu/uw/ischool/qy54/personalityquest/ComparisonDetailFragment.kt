package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import androidx.fragment.app.Fragment

class ComparisonDetailFragment : Fragment() {
    private lateinit var fetchedUserName: TextView
    private lateinit var fetchedUserMBTI: TextView
    private lateinit var userName: TextView
    private lateinit var userMBTI: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comparisondetail, container, false)

        fetchedUserName = view.findViewById(R.id.fetchedUserName)
        fetchedUserMBTI = view.findViewById(R.id.fetchedUserMBTI)
        userName = view.findViewById(R.id.userName)
        userMBTI = view.findViewById(R.id.userMBTI)

        val userResult = arguments?.getSerializable("userResult") as? UserResult
        val currentUserMBTI = arguments?.getString("currentUserMBTI")

        userResult?.let {
            fetchedUserName.text = it.name
            fetchedUserMBTI.text = it.mbtiType
        }
        userName.text = "Your MBTI"
        userMBTI.text = currentUserMBTI ?: "N/A"

        return view
    }
}


