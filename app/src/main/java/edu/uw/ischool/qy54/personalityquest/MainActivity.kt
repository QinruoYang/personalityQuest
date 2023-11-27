package edu.uw.ischool.qy54.personalityquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // How to get the quetions data
        val repo = (application as QuizApp).accessRepo()
        repo.refreshrepo("questions.json")

        val questions = repo.getQuestions()
        Log.i("test", questions.toString())


        // Initialize BottomNavigationView
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    // Handle back stack
    override fun onBackPressed() {
        if (!findNavController(R.id.nav_host_fragment).navigateUp()) {
            super.onBackPressed()
        }
    }


}