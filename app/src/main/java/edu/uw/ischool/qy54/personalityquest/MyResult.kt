package edu.uw.ischool.qy54.personalityquest

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
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
    private lateinit var buttonOpenSharePage: Button
    private lateinit var btnRestartQuiz: Button
    private lateinit var buttonSaveResult: Button
    private lateinit var buttonSend:Button
    private lateinit var editTextPhone:EditText
    private lateinit var buttonSendMessage:Button
    private lateinit var buttonSeeExplanation: Button

    private val REQUEST_PERMISSION = 1001

    private val sharedPrefs by lazy {
        (requireActivity().application as QuizApp).sharedPrefs
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_result, container, false)
        textResult = view.findViewById(R.id.text_my_result)

        //get result from preference
        val mbtiResult = sharedPrefs.getString("mbtiType", "You haven't taken a quiz yet.")
        textResult.text = "Your MBTI Type: $mbtiResult"

        buttonOpenSharePage = view.findViewById(R.id.buttonOpenSharePage)
        btnRestartQuiz = view.findViewById(R.id.btnRestartQuiz)
        buttonSaveResult = view.findViewById(R.id.buttonSaveResult)
        buttonSeeExplanation = view.findViewById(R.id.buttonSeeExplanation)

        buttonOpenSharePage.setOnClickListener {
            navigateToShareFragment()
        }

        btnRestartQuiz.setOnClickListener {
            restartQuiz()
        }

        buttonSaveResult.setOnClickListener {
            takeAndSaveScreenshot()
        }

        buttonSeeExplanation.setOnClickListener {
            navigateToPersonalityDetailFragment()
        }

        buttonSend = view.findViewById<Button>(R.id.buttonSend)
        editTextPhone = view.findViewById<EditText>(R.id.editTextPhone)
        buttonSendMessage = view.findViewById<Button>(R.id.buttonSendMessage)

        // Handle btn enable control
        if(mbtiResult != "You haven't taken a quiz yet.") {
            btnRestartQuiz.isEnabled = true;
            buttonOpenSharePage.isEnabled = true;
            buttonSend.isEnabled = true;
            buttonSaveResult.isEnabled = true;
            buttonSeeExplanation.isEnabled = true;
        }

        val watcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length == 10) {
                    buttonSendMessage.isEnabled = true;
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        }

        editTextPhone.addTextChangedListener(watcher)

        buttonSend.setOnClickListener {
            editTextPhone.visibility = View.VISIBLE
            buttonSendMessage.visibility = View.VISIBLE
        }

        buttonSendMessage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS), REQUEST_PERMISSION)
            } else {
                if(textResult.text.toString() != "You haven't taken a quiz yet." ) {
                    val phoneNumber = editTextPhone.text.toString()
                    val messageToSend = textResult.text.toString().replace("Your", "my")
                    val smsManager: SmsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phoneNumber, null, messageToSend, null, null)
                } else {

                    Toast.makeText(requireContext(), "You haven't taken a quiz yet.", Toast.LENGTH_SHORT).show()
                }
            }


        }

        return view
    }

    private fun navigateToShareFragment() {
        // Use MBTI result from SharedPreferences
        val result = sharedPrefs.getString("mbtiType", null)
        if (result != null) {
            val bundle = Bundle().apply {
                putString("mbtiResult", result)
            }
            findNavController().navigate(R.id.action_myResultFragment_to_shareFragment, bundle)
        }
    }

    private fun restartQuiz() {
        sharedPrefs.edit().remove("mbtiType").apply()
        findNavController().navigateUp()
    }

    private fun takeAndSaveScreenshot() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
            return
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val resolver = requireContext().contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            uri?.let { contentUri ->
                resolver.openOutputStream(contentUri)?.use { outputStream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        showToast("Failed to save bitmap.")
                    } else {
                        showToast("Screenshot saved successfully.")
                    }
                } ?: showToast("Failed to open output stream.")
            } ?: showToast("Failed to create media entry.")
        } else {
            val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YourAppName")
            if (!storageDir.exists()) storageDir.mkdirs()

            val imageFile = File(storageDir, fileName)
            FileOutputStream(imageFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                showToast("Screenshot saved to: ${imageFile.absolutePath}")
            }
        }
    }

    private fun galleryAddPic(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PersonalityQuest/")
            }
            val resolver = requireContext().contentResolver
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                mediaScanIntent.data = Uri.fromFile(file)
                requireContext().sendBroadcast(mediaScanIntent)
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                saveScreenshotToStorage(takeScreenshot())
            } else {
                showToast("Permission denied. Cannot save screenshot.")
            }
        }
    }

    private fun navigateToPersonalityDetailFragment() {
        val result = sharedPrefs.getString("mbtiType", null)
        if (result != null) {
            val bundle = bundleOf("PERSONALITY_TYPE" to result)
            findNavController().navigate(R.id.action_myResultFragment_to_personalityDetailFragment, bundle)
        }
    }
}