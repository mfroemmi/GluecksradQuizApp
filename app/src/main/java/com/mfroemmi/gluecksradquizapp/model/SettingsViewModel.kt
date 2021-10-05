package com.mfroemmi.gluecksradquizapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _screenHeight = MutableLiveData<Int>()
    val screenHeight: LiveData<Int> = _screenHeight

    private val _mode = MutableLiveData("question")
    val mode: LiveData<String> = _mode

    private val _tryLeftover = MutableLiveData(0)
    val tryLeftover: LiveData<Int> = _tryLeftover

    private val _intensity = MutableLiveData(1)
    val intensity: LiveData<Int> = _intensity

    fun setScreenHeight(screenHeight: Int) {
        _screenHeight.value = screenHeight
    }

    fun setMode(mode: String) {
        _mode.value = mode
    }

    fun getMode(): String? {
        return _mode.value
    }

    fun setTryLeftover(tryLeftover: Int) {
        _tryLeftover.value = tryLeftover
    }

    fun setIntensity(intensity: Int) {
        _intensity.value = intensity
    }

    fun getQuestions(): ArrayList<String> {
        val questionList = ArrayList<String>()

        questionList.add("Hexadezimal d")
        questionList.add("Quersumme aus 1024")
        questionList.add("29 % 5")
        questionList.add("Römisch XXIII")
        questionList.add("Binär 110101")
        questionList.add("0 - 1 - 1 - 2 - 3 - 5 - 8 - ?")
        questionList.add("5.725^0")
        questionList.add("So viele Hobbits sind ein Hobbyte")

        return questionList
    }

    fun getAnswers(): ArrayList<String> {
        val answerList = ArrayList<String>()

        answerList.add("13")
        answerList.add("7")
        answerList.add("4")
        answerList.add("23")
        answerList.add("53")
        answerList.add("13")
        answerList.add("1")
        answerList.add("8")

        return answerList
    }

    fun getScore(): ArrayList<String> {
        val questionList = ArrayList<String>()

        questionList.add("50")
        questionList.add("100")
        questionList.add("300")
        questionList.add("25")
        questionList.add("75")
        questionList.add("200")
        questionList.add("50")
        questionList.add("100")

        return questionList
    }

}