package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class PersonalityDetailFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personality_detail, container, false)
        val personalityType = arguments?.getString("PERSONALITY_TYPE")

        view.findViewById<TextView>(R.id.textViewTitle).text = personalityType


        val imageResId = resources.getIdentifier("${personalityType?.toLowerCase()}_person", "drawable", context?.packageName)
        view.findViewById<ImageView>(R.id.imageViewPersonality).setImageResource(imageResId)

        return view
    }






}