package edu.uw.ischool.qy54.personalityquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var shouldSelectTypes = false

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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.personalityDetailFragment) {
                navView.selectedItemId = R.id.navigation_types
            }
        }

        navView.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id == R.id.personalityDetailFragment
                && item.itemId == R.id.navigation_types) {

                true
            } else {

                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

//        navView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_quiz -> {
//                    // Check if we are not already on the Quiz fragment
//                    if (navController.currentDestination?.id != R.id.navigation_quiz) {
//                        navController.navigate(R.id.navigation_quiz)
//                    }
//                    true
//                }
//                else -> NavigationUI.onNavDestinationSelected(item, navController)
//            }
//        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.personalityDetailFragment -> {
                    // Set the selected item based on the source of navigation
                    val sourceFragmentId = navController.previousBackStackEntry?.destination?.id
                    when (sourceFragmentId) {
                        R.id.navigation_types -> navView.selectedItemId = R.id.navigation_types
                        R.id.navigation_my_result -> navView.selectedItemId = R.id.navigation_my_result
                        // Add cases for other fragments if needed
                    }
                }
                // Handle other destinations if needed
            }
        }

    }

    // Handle back stack
    override fun onBackPressed() {
        if (!findNavController(R.id.nav_host_fragment).navigateUp()) {
            super.onBackPressed()
        }
    }

    private fun resetQuizState() {
        val sharedPrefs = (application as QuizApp).sharedPrefs
        sharedPrefs.edit().apply {
            remove("mbtiType") // Clearing the quiz state
            apply()
        }
    }


}