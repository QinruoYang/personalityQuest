package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import androidx.fragment.app.Fragment
import org.json.JSONArray
import java.io.IOException

class ComparisonDetailFragment : Fragment() {
    private lateinit var fetchedUserName: TextView
    private lateinit var fetchedUserMBTI: TextView
    private lateinit var userName: TextView
    private lateinit var userMBTI: TextView
    private lateinit var compatibilityText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comparisondetail, container, false)

        fetchedUserName = view.findViewById(R.id.fetchedUserName)
        fetchedUserMBTI = view.findViewById(R.id.fetchedUserMBTI)
        userName = view.findViewById(R.id.userName)
        userMBTI = view.findViewById(R.id.userMBTI)
        compatibilityText = view.findViewById(R.id.compatibilityText)

        val userResult = arguments?.getSerializable("userResult") as? UserResult
        val currentUserMBTI = arguments?.getString("currentUserMBTI")

        userResult?.let {
            fetchedUserName.text = it.name
            fetchedUserMBTI.text = it.mbtiType
        }
        userName.text = "Your MBTI"
        userMBTI.text = currentUserMBTI ?: "N/A"

        loadCompatibilityInfo(userResult?.mbtiType ?: "", currentUserMBTI ?: "")

        return view
    }

    private fun loadCompatibilityInfo(mbtiType1: String, mbtiType2: String) {
        try {
            val assetManager = context?.assets
            val inputStream = assetManager?.open("mbti_compatibility_analysis.json")
            val json = inputStream?.bufferedReader().use { it?.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val pairing = jsonObject.getString("pairing")
                if (pairing == "$mbtiType1-$mbtiType2" || pairing == "$mbtiType2-$mbtiType1") {
                    val compatibility = jsonObject.getJSONObject("compatibility")
                    val compatibilityString = "Communication: ${compatibility.getString("communication")}\n\n" +
                            "Work: ${compatibility.getString("work")}\n\n" +
                            "Relationships: ${compatibility.getString("relationships")}"
                    compatibilityText.text = compatibilityString
                    break
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            compatibilityText.text = "Compatibility information not available."
        }
    }
}


