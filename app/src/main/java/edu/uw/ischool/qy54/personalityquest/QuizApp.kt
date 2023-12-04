package edu.uw.ischool.qy54.personalityquest

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class QuizApp: Application() {
    private lateinit var repository: TopicRepository
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE)
        repository = MockTopicRepository(this)
    }

    fun accessRepo(): TopicRepository {
        return repository
    }
}



interface TopicRepository {
    fun refreshrepo(fileName: String)
    fun getQuestions(): MutableList<Question>


}




class MockTopicRepository(private val context: Context): TopicRepository {
    val items:MutableList<Question> = mutableListOf( Question(
        questionText = "Error occure",
        choices = listOf("A","B"),
        category = listOf("E","I")
        ))

    init {
        refreshrepo("questions.json")
    }

    override fun refreshrepo(fileName: String) {
        download()
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val jsonString = file.readText(Charsets.UTF_8)
            // Clear the hardcoded list
            items.clear()

            // Parse the JSON string and add to 'items'
            parseQuestions(jsonString).let {
                items.addAll(it)
            }
        } else {

        }
    }

    // Download the data file from github
    private fun download() {
        val executor : Executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Everything in here is now on a different (non-UI)
            // thread and therefore safe to hit the network
            try {
                val urll =
                    URL("https://raw.githubusercontent.com/QinruoYang/personalityQuest/master/mbti_questions_complete-2.json")
                val connection = urll.openConnection() as HttpURLConnection
                connection.connect()

                val inputStream = connection.inputStream
                val file = File(context.filesDir, "questions.json")

                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                inputStream.close()
            } catch (e: Exception) {
                Log.e("DownloadError", "Error during download", e)

            }
        }
    }

    private fun parseQuestions(jsonString: String): List<Question> {
        val jsonArray = JSONArray(jsonString)
        return List(jsonArray.length()) { index ->
            jsonArray.getJSONObject(index).let { jsonObject ->
                Question(
                    questionText = jsonObject.getString("question"),
                    choices = jsonObject.getJSONArray("choices").let { choicesArray ->
                        List(choicesArray.length()) { choicesIndex ->
                            choicesArray.getString(choicesIndex)
                        }
                    },
                    category = jsonObject.getJSONArray("category").let { categoryArray ->
                        List(categoryArray.length()) { categoryIndex ->
                            categoryArray.getString(categoryIndex)
                        }
                    }
                )
            }
        }
    }

    //                    Above are functional tool funs

    override fun getQuestions(): MutableList<Question> {
        return items;
    }






}


// Models for Question
data class Question(val questionText: String,
                val choices: List<String>,
                val category: List<String>)
