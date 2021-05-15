package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel: ViewModel() {
    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1_000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 60_000L

        const val COUNTDOWN_PANIC_TIME = 5_000L
    }

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
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

    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    private val _score = MutableLiveData<Int>()
    private val _word = MutableLiveData<String>()
    private val _eventGameFinished = MutableLiveData<Boolean>()
    private val _remainingTime = MutableLiveData<Long>()
    private val _eventBuzz = MutableLiveData<BuzzType>()

    val remainingTimeString = Transformations.map(_remainingTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        _eventBuzz.value = BuzzType.NO_BUZZ
        _eventGameFinished.value = false
        resetList()
        nextWord()
        _score.value = 0

        timer = object: CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished == COUNTDOWN_PANIC_TIME) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
                _remainingTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _eventBuzz.value = BuzzType.GAME_OVER
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
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
}