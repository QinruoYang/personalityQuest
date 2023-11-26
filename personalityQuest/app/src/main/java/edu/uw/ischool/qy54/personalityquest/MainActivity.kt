package edu.uw.ischool.qy54.personalityquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // How to get the quetions data
        val repo = (application as QuizApp).accessRepo()
        repo.refreshrepo("questions.json")

        val questions = repo.getQuestions()
        Log.i("test", questions.toString())
    }
}