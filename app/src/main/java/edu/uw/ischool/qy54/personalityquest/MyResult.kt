package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class MyResult : Fragment() {
    private lateinit var textResult: TextView
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var buttonOpenSharePage: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_result, container, false)
        textResult = view.findViewById(R.id.text_my_result)

        // Initialize ViewModel
        quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

        // Observe the MBTI result
        quizViewModel.mbtiResult.observe(viewLifecycleOwner) { result ->
            textResult.text = if (result.isNullOrEmpty()) {
                "You haven't taken a quiz yet."
            } else {
                "Your MBTI Type: $result"
            }
        }

        buttonOpenSharePage = view.findViewById(R.id.buttonOpenSharePage)

        buttonOpenSharePage.setOnClickListener {
            navigateToShareFragment()
        }

        return view
    }

    private fun navigateToShareFragment() {
        val action = MyResultDirections.actionMyResultFragmentToShareFragment()
        quizViewModel.mbtiResult.value?.let { result ->
            val bundle = Bundle()
            bundle.putString("mbtiResult", result)
            findNavController().navigate(R.id.action_myResultFragment_to_shareFragment, bundle)
        }
    }

}