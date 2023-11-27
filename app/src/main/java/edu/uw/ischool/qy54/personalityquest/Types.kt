package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController

class Types : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.types, container, false)

        view.findViewById<TextView>(R.id.intj).setOnClickListener {
            navigateToPersonalityFragment("INTJ")
        }
        view.findViewById<TextView>(R.id.intp).setOnClickListener {
            navigateToPersonalityFragment("INTP")
        }
        view.findViewById<TextView>(R.id.entj).setOnClickListener {
            navigateToPersonalityFragment("ENTJ")
        }
        view.findViewById<TextView>(R.id.entp).setOnClickListener {
            navigateToPersonalityFragment("ENTP")
        }
        view.findViewById<TextView>(R.id.infj).setOnClickListener {
            navigateToPersonalityFragment("INFJ")
        }
        view.findViewById<TextView>(R.id.infp).setOnClickListener {
            navigateToPersonalityFragment("INFP")
        }
        view.findViewById<TextView>(R.id.enfj).setOnClickListener {
            navigateToPersonalityFragment("ENFJ")
        }
        view.findViewById<TextView>(R.id.enfp).setOnClickListener {
            navigateToPersonalityFragment("ENFP")
        }

        view.findViewById<TextView>(R.id.istj).setOnClickListener {
            navigateToPersonalityFragment("ISTJ")
        }
        view.findViewById<TextView>(R.id.isfj).setOnClickListener {
            navigateToPersonalityFragment("ISFJ")
        }
        view.findViewById<TextView>(R.id.estj).setOnClickListener {
            navigateToPersonalityFragment("ESTJ")
        }
        view.findViewById<TextView>(R.id.esfj).setOnClickListener {
            navigateToPersonalityFragment("ESFJ")
        }

        view.findViewById<TextView>(R.id.istp).setOnClickListener {
            navigateToPersonalityFragment("ISTP")
        }
        view.findViewById<TextView>(R.id.isfp).setOnClickListener {
            navigateToPersonalityFragment("ISFP")
        }
        view.findViewById<TextView>(R.id.estp).setOnClickListener {
            navigateToPersonalityFragment("ESTP")
        }
        view.findViewById<TextView>(R.id.esfp).setOnClickListener {
            navigateToPersonalityFragment("ESFP")
        }


        return view
    }

    private fun navigateToPersonalityFragment(personalityType: String) {
        val bundle = bundleOf("PERSONALITY_TYPE" to personalityType)
        findNavController().navigate(R.id.action_typesFragment_to_personalityDetailFragment, bundle)
    }

}