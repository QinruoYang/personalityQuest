package edu.uw.ischool.qy54.personalityquest

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyResult : Fragment() {
    private lateinit var textResult: TextView
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var buttonOpenSharePage: Button
    private lateinit var btnRestartQuiz: Button
    private lateinit var buttonSaveResult: Button

    private val REQUEST_PERMISSION = 1001

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
        btnRestartQuiz = view.findViewById(R.id.btnRestartQuiz)
        buttonSaveResult = view.findViewById(R.id.buttonSaveResult)

        buttonOpenSharePage.setOnClickListener {
            navigateToShareFragment()
        }

        btnRestartQuiz.setOnClickListener {
            restartQuiz()
        }

        buttonSaveResult.setOnClickListener {
            takeAndSaveScreenshot()
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

    private fun restartQuiz() {
        quizViewModel.setMBTIResult("")
        findNavController().navigateUp()
    }

    private fun takeAndSaveScreenshot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION
                )
                return
            }
        }
        saveScreenshotToStorage(takeScreenshot())
    }

    private fun takeScreenshot(): Bitmap {
        val rootView = requireActivity().window.decorView.rootView
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun saveScreenshotToStorage(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Screenshot_$timeStamp.png"

        val storageDir =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/PersonalityQuest/")
        storageDir.mkdirs()

        val filePath = File(storageDir, fileName)

        try {
            val stream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            showToast("Screenshot saved to: ${filePath.absolutePath}")
        } catch (e: Exception) {
            showToast("Failed to save screenshot: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}