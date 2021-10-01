package com.mfroemmi.gluecksradquizapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mfroemmi.gluecksradquizapp.databinding.FragmentSpinBinding
import com.mfroemmi.gluecksradquizapp.model.QuestionsModel
import com.mfroemmi.gluecksradquizapp.model.SettingsViewModel
import io.objectbox.Box
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.koin.android.ext.android.inject
import kotlin.math.exp
import kotlin.math.pow

class SpinFragment : Fragment() {

    private var binding: FragmentSpinBinding? = null
    private val sharedViewModel: SettingsViewModel by activityViewModels()

    private val questionBox: Box<QuestionsModel> by inject("questionsModel")

    private var spinTimer: CountDownTimer? = null

    private var y = 0
    private var y0 = 0
    private var screenHeight: Int? = null
    private var highlightField = 0.0F
    private var lastPossibleDegree = 0.0F

    private var isSpin = true
    private var isMove = false
    private var isLeft = false

    private var mTryLeftover: Int? = null
    private var mIntensity: Int? = null
    private var mMode: String? = null

    private lateinit var questionList: ArrayList<String>
    private lateinit var questionListNormal: ArrayList<String>
    private lateinit var scoreList: ArrayList<String>
    private var rotationList: MutableList<Float> = ArrayList()

    private var soundPool: SoundPool? = null
    private val soundId = 1

    private var winPlayer: MediaPlayer? = null
    private var questionPlayer: MediaPlayer? = null
    private var losePlayer: MediaPlayer? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentSpinBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        // Initialisierung des -soundPool- Players zum abspielen des Spinsounds
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(context, R.raw.spinsound, 1)

        mTryLeftover = sharedViewModel.tryLeftover.value
        mIntensity = sharedViewModel.intensity.value
        mMode = sharedViewModel.mode.value

        questionListNormal = ArrayList()
        for (i in 0..questionBox.all.size-1) {
            questionListNormal.add(questionBox.all[i].question)
            println(questionBox.all[i].question)
        }
        questionList = questionListNormal
        scoreList = sharedViewModel.getScore()

        if (mMode == "score") {
            binding!!.ivWheel.setImageResource(R.drawable.wheel_score)
            binding!!.tvTries.visibility = View.VISIBLE
            binding!!.tvScore.visibility = View.VISIBLE
            setTriesNumber()
            setIntensNumber()
        } else {
            binding!!.ivWheel.setImageResource(R.drawable.wheel)
            binding!!.tvTries.visibility = View.INVISIBLE
            binding!!.tvScore.visibility = View.INVISIBLE
            setIntensNumber()
        }

        // Speichert die Displayhöhe in screen_height
        screenHeight = sharedViewModel.screenHeight.value

        // Klicken des -DREHEN- Buttons
        binding!!.btnReset.setOnClickListener {
            visibilityOptionBar(false)
            isSpin = false
        }

        // Wenn Frage aktiv ist -> Klick auf den -Check- Button
        binding!!.ivPopupCancel.setOnClickListener {
            binding!!.ivWheel.rotation = 0.0F
            if (mTryLeftover!! > 0) {
                setQuestionPopup(true, 1)
                isSpin = false
            } else {
                mTryLeftover = sharedViewModel.tryLeftover.value
                binding!!.tvScore.text = "0"
                setTriesNumber()
                setQuestionPopup(true, 1)
                visibilityOptionBar(true)
            }
        }

        // Über den Bildschirm wischen, um das Glücksrad zu bewegen - Rechts
        binding!!.flTouch.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            if (!isSpin) {
                isLeft = false
                questionList = questionListNormal
                scoreList = sharedViewModel.getScore()
                when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE -> {
                        isMove = true
                        y = motionEvent.rawY.toInt()
                        binding!!.ivWheel.rotation = getRotationInDegree()
                        startSpinPlayerLive()
                    }
                    MotionEvent.ACTION_DOWN -> {
                        y0 = motionEvent.rawY.toInt()
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isMove) {
                            startSpinTimer(getSpinValues(rotationList))
                            rotationList.clear()
                            isSpin = true
                            isMove = false
                        }

                    }
                }
            }
            return@OnTouchListener true
        })

        // Über den Bildschirm wischen, um das Glücksrad zu bewegen - Links
        binding!!.flTouchLeft.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            if (!isSpin) {
                isLeft = true
                questionList = questionListNormal
                questionList.reverse()
                scoreList = sharedViewModel.getScore()
                scoreList.reverse()
                when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE -> {
                        isMove = true
                        y = motionEvent.rawY.toInt()
                        binding!!.ivWheel.rotation = -getRotationInDegree()
                        startSpinPlayerLive()
                    }
                    MotionEvent.ACTION_DOWN -> {
                        y0 = motionEvent.rawY.toInt()
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isMove) {
                            startSpinTimer(getSpinValues(rotationList))
                            rotationList.clear()
                            isSpin = true
                            isMove = false
                        }

                    }
                }
            }
            return@OnTouchListener true
        })

        return binding!!.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewModel = sharedViewModel
            spinFragment = this@SpinFragment
        }
    }

    fun goToStartFragment() {
        findNavController().navigate(R.id.action_spinFragment_to_startFragment)
    }

    private fun startSpinTimer(spinValues: MutableList<Any>) {
        var i = 0.0F
        var valueGauss: Float
        var valueRotation: Float
        spinTimer = object : CountDownTimer(35000, 10) {
            override fun onTick(p0: Long) {

                startSpinPlayerLive()

                // Wird in die falsche Richtung gedreht, dann wird der Spintimer nicht ausgeführt
                if (spinValues[0] == 0) {
                    isSpin = false
                    spinTimer?.cancel()
                }

                if (isLeft) {
                    // Lässt das Glückrad anhand der Gauß-Funktion rotieren
                    binding!!.ivWheel.rotation -= (spinValues[0].toString()
                        .toFloat() * getGauss(i))
                } else {
                    // Lässt das Glückrad anhand der Gauß-Funktion rotieren
                    binding!!.ivWheel.rotation += (spinValues[0].toString()
                        .toFloat() * getGauss(i))
                }

                valueGauss = getGauss(i)
                valueRotation = getBackRotation(binding!!.ivWheel.rotation)

                // Verhindert das der Zeiger in der Mitte zwischen zwei Feldern stehen bleibt
                if (valueGauss < 0.1 && valueRotation > 0.5 && valueRotation < 0.55) {
                } else if (isLeft && valueGauss < 0.1 && valueRotation > -0.50 && valueRotation < -0.40) {
                } else {
                    i += spinValues[1].toString().toFloat()
                }

                // Stoppt den Spintimer wenn die Rotation beendet ist
                if (valueGauss <= 0.001) {
                    stopSound()
                    spinTimer?.onFinish()
                }

            }

            override fun onFinish() {
                // Speichert den aktuellen Rotationswert des getroffenen Feldes
                if (isLeft) {
                    // Speichert den aktuellen Rotationswert des getroffenen Feldes
                    highlightField = -getHighlightField(binding!!.ivWheel.rotation)
                    // Setzt das Highlight auf die mittlere Position des getroffenen Feldes
                    binding!!.ivHighlight.rotation =
                        -getHighlightRotation(highlightField)
                } else {
                    // Speichert den aktuellen Rotationswert des getroffenen Feldes
                    highlightField = getHighlightField(binding!!.ivWheel.rotation)
                    // Setzt das Highlight auf die mittlere Position des getroffenen Feldes
                    binding!!.ivHighlight.rotation =
                        getHighlightRotation(highlightField)
                }

                binding!!.ivHighlight.visibility = View.VISIBLE

                // Startet den passenden Sound des getroffenen Feldes
                setHighlightSound(highlightField)

                setHighlightScore(highlightField)

                mTryLeftover = mTryLeftover!! - 1
                setTriesNumber()

                endSpinTimer()
                spinTimer!!.cancel()
            }
        }.start()
    }

    private fun endSpinTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            setHighlightField(highlightField)
            stopSound()
        }, 3100)
    }

    private fun getSpinValues(rotationList: MutableList<Float>): MutableList<Any> {

        val list: MutableList<Float> = ArrayList()
        val randomNumber = ((-20..20).random()).toFloat() / 10
        val intens = when (mIntensity.toString().toInt()) {
            1 -> 0.0
            2 -> 0.001
            3 -> 0.003
            else -> 0.0
        }

        // Fügt der Liste die drei Wertepaare hinzu
        for (i in 0 downTo -2) {
            list.add(
                rotationList[rotationList.lastIndex + i] -
                        rotationList[rotationList.lastIndex + (i - 1)]
            )
        }

        // Berechnet den Mittelwert und gibt die entsprechenden -SpinValues- zurück
        var average = (list.sum() / 3).toInt()

        if (isLeft) {
            average *= -1
        }

        /* Werte der MutableList(Dauer der Drehung, Multiplikation mit dem Gauß-Wert,
         * Variable zur Berechnung des Gauß-Wertes)
         */
        return when (average) {
            in -100..-1 -> mutableListOf(0, 0.0)
            in 0..1 -> mutableListOf(5 + randomNumber, 0.01 - intens)
            in 2..4 -> mutableListOf(5.5 + randomNumber, 0.0095 - intens)
            in 5..9 -> mutableListOf(6 + randomNumber, 0.009 - intens)
            in 10..15 -> mutableListOf(7 + randomNumber, 0.008 - intens)
            in 16..25 -> mutableListOf(8 + randomNumber, 0.007 - intens)
            in 26..35 -> mutableListOf(9 + randomNumber, 0.006 - intens)
            in 36..45 -> mutableListOf(10 + randomNumber, 0.005 - intens)
            else -> mutableListOf(11 + randomNumber, 0.004 - intens)
        }

    }

    private fun setQuestionPopup(isQuestion: Boolean, question: Int) {
        /* Wenn eine Frage getroffen wurde dann wird der Popup-Screen eingeblendet
         * ist die Frage nicht mehr aktiv, wird sie ausgeblendet
         */
        if (isQuestion) {
            binding!!.vPopupScreen.visibility = View.GONE
            binding!!.llPopupQuestion.visibility = View.GONE
            binding!!.ivPopupCancel.visibility = View.GONE
            binding!!.ivHighlight.visibility = View.INVISIBLE
        } else {
            binding!!.tvPopupQuestion.text = questionList[question]
            binding!!.vPopupScreen.visibility = View.VISIBLE
            binding!!.llPopupQuestion.visibility = View.VISIBLE
            binding!!.ivPopupCancel.visibility = View.VISIBLE
        }
    }

    private fun setWinLosePopup() {
        binding!!.ivPopupCancel.visibility = View.VISIBLE
    }

    private fun setScorePopup(field: Int) {
        val currentScore = binding!!.tvScore.text.toString().toInt()
        if (field == 10) {
            binding!!.tvScore.text = (currentScore + 500).toString()
        } else {
            binding!!.tvScore.text = (currentScore + scoreList[field].toInt()).toString()
        }

        binding!!.ivPopupCancel.visibility = View.VISIBLE
    }

    private fun getRotationInDegree(): Float {
        return if (y > 0.0F) {
            val roundInt = ((360.0F / screenHeight!! * (y - y0)) / 2).toInt()
            roundInt.toFloat()
        } else {
            val roundInt = (((360.0F / screenHeight!! * (y - y0)) + 360.0F) / 2).toInt()
            roundInt.toFloat()
        }
    }

    private fun getHighlightRotation(netWheelRotation: Float): Float {

        val factorRotation = (netWheelRotation / 30) % 1

        return if (factorRotation in 0.0001..0.52) {
            30 * factorRotation
        } else {
            30 * (factorRotation - 1)
        }
    }

    private fun getBackRotation(wheelRotation: Float): Float {
        val netWheelRotation = wheelRotation - ((wheelRotation / 360.0F).toInt() * 360)

        return (netWheelRotation / 30) % 1
    }

    private fun getHighlightField(wheelRotation: Float): Float {

        return wheelRotation - ((wheelRotation / 360.0F).toInt() * 360)

    }

    private fun setTriesNumber() {
        binding!!.tvTries.text = mTryLeftover.toString()
    }

    private fun setIntensNumber() {
        binding!!.tvIntensity.text = mIntensity.toString()
    }

    private fun setHighlightSound(wheelRotation: Float) {
        when (wheelRotation) {
            in 345.0001F..360.4F -> startWinSound() // Hauptgewinn (1)
            in 0.00F..15.4F -> {
                startWinSound()
                startKonfettiAnimation()
            } // Hauptgewinn (1)
            in 15.0001F..45.4F -> startLoseSound() // Niete (12)
            in 45.0001F..75.4F -> startQuestionSound() // Frage (11)
            in 75.0001F..105.4F -> startQuestionSound() // Frage (10)
            in 105.0001F..135.4F -> startQuestionSound() // Frage (9)
            in 135.0001F..165.4F -> startQuestionSound() // Frage (8)
            in 165.0001F..195.4F -> startLoseSound() // Niete (7)
            in 195.0001F..225.4F -> startQuestionSound() // Frage (6)
            in 225.0001F..255.4F -> startQuestionSound() // Frage (5)
            in 255.0001F..285.4F -> startQuestionSound() // Frage (4)
            in 285.0001F..315.4F -> startQuestionSound() // Frage (3)
            in 315.0001F..345.4F -> startLoseSound() // Niete (2)
        }
    }

    private fun setHighlightScore(wheelRotation: Float) {
        if (mMode == "score") {
            when (wheelRotation) {
                in 345.0001F..360.4F -> setScorePopup(10) // Hauptgewinn (1)
                in 0.00F..15.4F -> setScorePopup(10) // Hauptgewinn (1)
                in 15.0001F..45.4F -> setWinLosePopup() // Niete (12)
                in 45.0001F..75.4F -> setScorePopup(0) // Frage (11)
                in 75.0001F..105.4F -> setScorePopup(1) // Frage (10)
                in 105.0001F..135.4F -> setScorePopup(2) // Frage (9)
                in 135.0001F..165.4F -> setScorePopup(3) // Frage (8)
                in 165.0001F..195.4F -> setWinLosePopup() // Niete (7)
                in 195.0001F..225.4F -> setScorePopup(4) // Frage (6)
                in 225.0001F..255.4F -> setScorePopup(5) // Frage (5)
                in 255.0001F..285.4F -> setScorePopup(6) // Frage (4)
                in 285.0001F..315.4F -> setScorePopup(7) // Frage (3)
                in 315.0001F..345.4F -> setWinLosePopup() // Niete (2)
            }
        }
    }

    private fun setHighlightField(wheelRotation: Float) {
        if (mMode == "question" || mMode == null) {
            when (wheelRotation) {
                in 345.0001F..360.4F -> setWinLosePopup() // Hauptgewinn (1)
                in 0.00F..15.4F -> setWinLosePopup() // Hauptgewinn (1)
                in 15.0001F..45.4F -> setWinLosePopup() // Niete (12)
                in 45.0001F..75.4F -> setQuestionPopup(false, 0) // Frage (11)
                in 75.0001F..105.4F -> setQuestionPopup(false, 1) // Frage (10)
                in 105.0001F..135.4F -> setQuestionPopup(false, 2) // Frage (9)
                in 135.0001F..165.4F -> setQuestionPopup(false, 3) // Frage (8)
                in 165.0001F..195.4F -> setWinLosePopup() // Niete (7)
                in 195.0001F..225.4F -> setQuestionPopup(false, 4) // Frage (6)
                in 225.0001F..255.4F -> setQuestionPopup(false, 5) // Frage (5)
                in 255.0001F..285.4F -> setQuestionPopup(false, 6) // Frage (4)
                in 285.0001F..315.4F -> setQuestionPopup(false, 7) // Frage (3)
                in 315.0001F..345.4F -> setWinLosePopup() // Niete (2)
            }
        }
    }

    private fun getGauss(i: Float): Float {
        return (exp(-i.pow(2)))
    }

    private fun getPossibleDegree(last: Float): Boolean {

        /* Ist der letzte Wert durch 30 teilbar und nicht der letzte der gespeichert wurde,
         * dann gib True zurück.
         */
        val possibleDegrees = (((last - 15) / 30).toInt() * 30).toFloat()

        if (lastPossibleDegree == possibleDegrees)
            return false

        lastPossibleDegree = possibleDegrees
        return true

    }

    private fun startSpinPlayerLive() {
        try {

            rotationList.add(binding!!.ivWheel.rotation)
            if (rotationList.size >= 2) {
                if (getPossibleDegree(rotationList[rotationList.lastIndex])
                ) {
                    soundPool?.play(soundId, 1.0F, 1.0F, 0, 0, 1.0F)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startWinSound() {
        try {
            winPlayer = MediaPlayer.create(context, R.raw.winsound)
            winPlayer!!.isLooping = false
            winPlayer!!.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startQuestionSound() {
        try {
            questionPlayer = MediaPlayer.create(context, R.raw.questionsound)
            questionPlayer!!.isLooping = false
            questionPlayer!!.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startLoseSound() {
        try {
            losePlayer = MediaPlayer.create(context, R.raw.losesound)
            losePlayer!!.isLooping = false
            losePlayer!!.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopSound() {
        if (questionPlayer != null) {
            questionPlayer!!.stop()
        }
        if (winPlayer != null) {
            winPlayer!!.stop()
        }
        if (losePlayer != null) {
            losePlayer!!.stop()
        }
    }

    private fun stopSpinSound() {
        if (soundPool != null) {
            soundPool?.release()
            soundPool = null
        }
    }

    private fun startKonfettiAnimation() {
        binding!!.konfettiView.build()
            .addColors(Color.YELLOW, Color.CYAN, Color.LTGRAY, Color.WHITE)
            .setDirection(0.0, 359.0)
            .setSpeed(1F, 5F)
            .setFadeOutEnabled(true)
            .setTimeToLive(1000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50F, binding!!.konfettiView.width + 50F, -50F, -50F)
            .streamFor(300, 3000L)
    }

    private fun visibilityOptionBar(visible: Boolean) {
        if (visible) {
            binding!!.vPopupScreen.visibility = View.VISIBLE
            binding!!.ivOptions.visibility = View.VISIBLE
            binding!!.btnBack.visibility = View.VISIBLE
            binding!!.btnReset.visibility = View.VISIBLE
        } else {
            binding!!.vPopupScreen.visibility = View.GONE
            binding!!.ivOptions.visibility = View.GONE
            binding!!.btnBack.visibility = View.GONE
            binding!!.btnReset.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}