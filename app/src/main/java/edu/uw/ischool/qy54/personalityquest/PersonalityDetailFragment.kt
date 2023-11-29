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

        val shortDescriptionResId = context?.resources?.getIdentifier("${personalityType?.toLowerCase()}_short_description", "string", context?.packageName)
        val longDescriptionResId = context?.resources?.getIdentifier("${personalityType?.toLowerCase()}_long_description", "string", context?.packageName)

        shortDescriptionResId?.let {
            view.findViewById<TextView>(R.id.textViewShortDescription).text = getString(it)
        }

        longDescriptionResId?.let {
            view.findViewById<TextView>(R.id.textViewLongDescription).text = getString(it)
        }

        for (i in 1..4) {
            val imageResId = resources.getIdentifier("${personalityType?.toLowerCase()}_famous_$i", "drawable", context?.packageName)
            val imageViewId = resources.getIdentifier("imageViewFamous$i", "id", context?.packageName)
            view.findViewById<ImageView>(imageViewId)?.setImageResource(imageResId)
        }

        return view
    }






}