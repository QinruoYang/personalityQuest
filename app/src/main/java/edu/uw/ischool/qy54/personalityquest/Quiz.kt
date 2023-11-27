package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class Quiz : Fragment() {
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

        // Display the first question
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
                    // Record the user's response when a choice is selected
                    recordUserResponse(question.category[question.choices.indexOf(choice)])
                }
            }
        }

        // Hide the "Next" button based on whether it's the last question
        btnNext.visibility = if (currentQuestionIndex == questions.size - 1) View.GONE else View.VISIBLE
    }

    private fun onNextButtonClick() {
        // Check if there are more questions
        if (currentQuestionIndex < questions.size - 1) {
            // Record the user's response for the current question
            recordUserResponse(questions[currentQuestionIndex].category[0])

            currentQuestionIndex++
            displayQuestion(questions[currentQuestionIndex])

            // Update the progress tracker
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
        val mbtiType = determineMBTIType()

        // Display the result directly in the quiz fragment
        textQuestion.text = "Your MBTI Type: $mbtiType"

        choicesGroup.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnBack.visibility = View.GONE
        btnFinish.visibility = View.GONE
        textProgress.visibility = View.GONE
        btnRestart.visibility = View.VISIBLE
    }

    private fun restartQuiz() {
        // Reset the quiz state to start again
        currentQuestionIndex = 0

        textQuestion.visibility = View.GONE
        choicesGroup.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnBack.visibility = View.GONE
        btnFinish.visibility = View.GONE
        textProgress.visibility = View.GONE
        btnStart.visibility = View.VISIBLE
        btnRestart.visibility = View.GONE
    }

    private fun determineMBTIType(): String {
        return calculateMBTIType()
    }

    private fun recordUserResponse(selectedCategory: String) {
        userResponses.add(selectedCategory)
    }

    private fun calculateMBTIType(): String {
        val categoryCounts = mutableMapOf<String, Int>()

        // Count occurrences of each category
        for (response in userResponses) {
            categoryCounts[response] = (categoryCounts[response] ?: 0) + 1
        }

        // Find the category with the highest count
        val maxCategory = categoryCounts.maxByOrNull { it.value }?.key

        // Return the MBTI type
        return maxCategory ?: "Unknown"
    }

}