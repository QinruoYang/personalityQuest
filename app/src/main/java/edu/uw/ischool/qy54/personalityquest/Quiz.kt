package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class Quiz : Fragment() {
    private lateinit var quizViewModel: QuizViewModel
    private val userResponses = mutableListOf<String>()

    private lateinit var repository: TopicRepository
    private lateinit var btnStart: Button
    private lateinit var textQuestion: TextView
    private lateinit var choicesGroup: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button
    private lateinit var btnFinish: Button
    private lateinit var textProgress: TextView
    private lateinit var btnRestart: Button

    private lateinit var questions: MutableList<Question>
    private var currentQuestionIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.quiz, container, false)

        // Initialize ViewModel
        quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

        // Observe the MBTI result
        quizViewModel.mbtiResult.observe(viewLifecycleOwner, { result ->
            // Update UI with the result
        })

        // Access the repository from the QuizApp
        repository = (requireActivity().application as QuizApp).accessRepo()

        btnStart = view.findViewById(R.id.btnStart)
        textQuestion = view.findViewById(R.id.textQuestion)
        choicesGroup = view.findViewById(R.id.choicesGroup)
        btnNext = view.findViewById(R.id.btnNext)
        btnBack = view.findViewById(R.id.btnBack)
        btnFinish = view.findViewById(R.id.btnFinish)
        textProgress = view.findViewById(R.id.textProgress)
        btnRestart = view.findViewById(R.id.btnRestart)

        textQuestion.visibility = View.GONE
        choicesGroup.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnBack.visibility = View.GONE
        btnFinish.visibility = View.GONE
        textProgress.visibility = View.GONE

        btnStart.setOnClickListener {
            startQuiz()
        }

        btnNext.setOnClickListener {
            onNextButtonClick()
        }

        btnBack.setOnClickListener {
            onBackButtonClick()
        }

        btnFinish.setOnClickListener {
            onFinishButtonClick()
        }

        btnRestart.setOnClickListener {
            restartQuiz()
        }

        return view
    }

    private fun updateProgress() {
        textProgress.text = "Question ${currentQuestionIndex + 1} of ${questions.size}"
    }

    private fun startQuiz() {
        questions = repository.getQuestions()

        btnStart.visibility = View.GONE
        textQuestion.visibility = View.VISIBLE
        choicesGroup.visibility = View.VISIBLE
        btnBack.visibility = View.VISIBLE
        btnFinish.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
        textProgress.visibility = View.VISIBLE

        updateProgress()
        displayQuestion(questions[currentQuestionIndex])
    }

    private fun displayQuestion(question: Question) {
        textQuestion.text = question.questionText
        choicesGroup.removeAllViews()

        // Add radio buttons for each choice
        for (choice in question.choices) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = choice
            choicesGroup.addView(radioButton)

            // Set a listener for each radio button
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Find the index of the selected radio button
                    val selectedIndex = choicesGroup.indexOfChild(radioButton)

                    // Use the index to get the corresponding category
                    val selectedCategory = question.category[selectedIndex]

                    // Record the selected category
                    recordUserResponse(selectedCategory)

                    println("Recorded Category: $selectedCategory")
                }
            }
        }

        // Hide the "Next" button based on whether it's the last question
        btnNext.visibility = if (currentQuestionIndex == questions.size - 1) View.GONE else View.VISIBLE
    }

    private fun onNextButtonClick() {
        // Check if there are more questions
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            displayQuestion(questions[currentQuestionIndex])

            updateProgress()
        } else {
            // All questions have been answered
            onFinishButtonClick()
        }

        // Check if it's the last question
        if (currentQuestionIndex == questions.size - 1) {
            // Calculate and display MBTI type when the quiz is completed
            calculateMBTIType()
        }
    }

    private fun onBackButtonClick() {
        if (currentQuestionIndex > 0) {
            // Move to the previous question
            currentQuestionIndex--

            // Display the previous question
            displayQuestion(questions[currentQuestionIndex])

            // Update the progress tracker
            updateProgress()
        }
    }

    private fun onFinishButtonClick() {
        // Determine the user's MBTI type based on their responses
        val mbtiType = calculateMBTIType()

        // Display the result directly in the quiz fragment
        // textQuestion.text = "Your MBTI Type: $mbtiType"

        // Update the result in ResultFragment
        quizViewModel.setMBTIResult(mbtiType)

        // Assuming you have a bundle defined in QuizFragment to pass the data
        val bundle = Bundle().apply {
            putString("mbtiType", mbtiType)
        }

        // Navigate to ResultFragment with the bundle
        findNavController().navigate(R.id.action_quizFragment_to_resultFragment, bundle)


        choicesGroup.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnBack.visibility = View.GONE
        btnFinish.visibility = View.GONE
        textProgress.visibility = View.GONE
        btnRestart.visibility = View.VISIBLE
    }

    private fun restartQuiz() {
        currentQuestionIndex = 0
        userResponses.clear()

        textQuestion.visibility = View.GONE
        choicesGroup.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnBack.visibility = View.GONE
        btnFinish.visibility = View.GONE
        textProgress.visibility = View.GONE
        btnStart.visibility = View.VISIBLE
        btnRestart.visibility = View.GONE
    }

    private fun recordUserResponse(selectedCategory: String) {
        userResponses.add(selectedCategory)
    }


    private fun calculateMBTIType(): String {
        val categoryCounts = mutableMapOf<Char, Int>()

        // Count occurrences of each letter in all responses
        for (response in userResponses) {
            for (letter in response) {
                categoryCounts[letter] = (categoryCounts[letter] ?: 0) + 1
            }
        }

        println("Final record:  $categoryCounts")

        // Determine the dominant letter in each dimension
        val dominantE = if (categoryCounts['E'] ?: 0 > categoryCounts['I'] ?: 0) 'E' else 'I'
        val dominantS = if (categoryCounts['S'] ?: 0 > categoryCounts['N'] ?: 0) 'S' else 'N'
        val dominantT = if (categoryCounts['T'] ?: 0 > categoryCounts['F'] ?: 0) 'T' else 'F'
        val dominantJ = if (categoryCounts['J'] ?: 0 > categoryCounts['P'] ?: 0) 'J' else 'P'

        // Build the MBTI type string
        return buildString {
            append(dominantE)
            append(dominantS)
            append(dominantT)
            append(dominantJ)
        }
    }

}