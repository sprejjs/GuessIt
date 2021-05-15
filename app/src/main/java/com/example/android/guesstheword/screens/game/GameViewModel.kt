package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1_000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 10_000L
    }

    private val timer: CountDownTimer

    // The current word
    val word : LiveData<String>
        get() = _word

    // The current score
    val score : LiveData<Int>
        get() = _score

    val gameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    private val _score = MutableLiveData<Int>()
    private val _word = MutableLiveData<String>()
    private val _eventGameFinished = MutableLiveData<Boolean>()
    private val _remainingTime = MutableLiveData<Long>()

    val remainingTimeString = Transformations.map(_remainingTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        _eventGameFinished.value = false
        resetList()
        nextWord()
        _score.value = 0

        timer = object: CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _remainingTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _eventGameFinished.value = true
            }
        }
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    fun onSkip() {
        _score.value = score.value?.minus(1) ?: -1
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1) ?: 1
        nextWord()
    }
}