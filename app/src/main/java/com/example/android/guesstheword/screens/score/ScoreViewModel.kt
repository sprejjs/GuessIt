package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(score: Int): ViewModel() {

    val finalScore: LiveData<Int>
        get() = _finalScore

    val playAgainEvent: LiveData<Boolean>
        get() = _playAgainEvent

    private val _playAgainEvent = MutableLiveData<Boolean>()
    private val _finalScore = MutableLiveData<Int>()

    init {
        Log.i("ScoreViewModel", "Final score is $score")
        _playAgainEvent.value = false
        _finalScore.value = score
    }

    fun onPlayAgain() {
        _playAgainEvent.value = true
    }
}