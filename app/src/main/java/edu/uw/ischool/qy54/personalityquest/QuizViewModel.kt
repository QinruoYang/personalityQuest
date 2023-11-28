package edu.uw.ischool.qy54.personalityquest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    private val _mbtiResult = MutableLiveData<String?>(null)
    val mbtiResult: LiveData<String?> get() = _mbtiResult

    fun setMBTIResult(result: String?) {
        _mbtiResult.value = result
    }
}