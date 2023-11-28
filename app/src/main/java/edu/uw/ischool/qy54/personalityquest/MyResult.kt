package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class MyResult : Fragment() {
    private lateinit var textResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.my_result, container, false)

        textResult = view.findViewById(R.id.text_my_result)


        val mbtiResult = requireArguments().getString("mbtiType")

        if (mbtiResult != null) {
            println("mbtiResult in ResultFragment: $mbtiResult")
            textResult.text = "Your MBTI Type: $mbtiResult"
        }

        return view
    }


}