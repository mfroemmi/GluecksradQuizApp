package com.mfroemmi.gluecksradquizapp

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.mfroemmi.gluecksradquizapp.databinding.FragmentQuestionListBinding
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel
import com.mfroemmi.gluecksradquizapp.model.QuestionsModel
import com.mfroemmi.gluecksradquizapp.model.SettingsViewModel
import io.objectbox.Box
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import java.text.SimpleDateFormat
import java.util.*

@KoinApiExtension
class QuestionListFragment : Fragment(), KoinComponent {

    private var binding: FragmentQuestionListBinding? = null
    private val sharedViewModel: SettingsViewModel by viewModel()

    private val questionBox: Box<QuestionsModel> by inject(named("questionsModel"))
    private val questionSetBox: Box<QuestionSetModel> by inject(named("questionSetModel"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentQuestionListBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        // Lädt die in der Datenbank gespeicherten Fragen in die Textfelder
        setQuestionListFromObjectBox()

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            questionListFragment = this@QuestionListFragment
        }
    }

    fun goToSettingsFragment() {

        if (binding!!.tvQuestion1Text.text!!.isEmpty() ||
            binding!!.tvQuestion2Text.text!!.isEmpty() ||
            binding!!.tvQuestion3Text.text!!.isEmpty() ||
            binding!!.tvQuestion4Text.text!!.isEmpty() ||
            binding!!.tvQuestion5Text.text!!.isEmpty() ||
            binding!!.tvQuestion6Text.text!!.isEmpty() ||
            binding!!.tvQuestion7Text.text!!.isEmpty() ||
            binding!!.tvQuestion8Text.text!!.isEmpty()
        ) {
            Toast.makeText(
                context,
                "Bitte gib in allen Textfeldern eine Frage ein",
                Toast.LENGTH_LONG
            ).show()
        } else {
            questionBox.removeAll()
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion1Text.text.toString(), answer = binding!!.tvQuestion1SecondaryText.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion2Text.text.toString(), answer = binding!!.tvQuestion2SecondaryText.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion3Text.text.toString(), answer = binding!!.tvQuestion3SecondaryText.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion4Text.text.toString(), answer = binding!!.tvQuestion4SecondaryText.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion5Text.text.toString(), answer = binding!!.tvQuestion5SecondaryText.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion6Text.text.toString(), answer = binding!!.tvQuestion6SecondaryText.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion7Text.text.toString(), answer = binding!!.tvQuestion7SecondaryText.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.tvQuestion8Text.text.toString(), answer = binding!!.tvQuestion8SecondaryText.text.toString()))

            findNavController().navigate(R.id.action_questionListFragment_to_settingsFragment)
        }

    }

    fun goToLoadQuestionSetFragment() {
        findNavController().navigate(R.id.action_questionListFragment_to_loadQuestionSetFragment)
    }

    fun goToSaveQuestionSetDialog() {
        if (binding!!.tvQuestion1Text.text!!.isEmpty() ||
            binding!!.tvQuestion2Text.text!!.isEmpty() ||
            binding!!.tvQuestion3Text.text!!.isEmpty() ||
            binding!!.tvQuestion4Text.text!!.isEmpty() ||
            binding!!.tvQuestion5Text.text!!.isEmpty() ||
            binding!!.tvQuestion6Text.text!!.isEmpty() ||
            binding!!.tvQuestion7Text.text!!.isEmpty() ||
            binding!!.tvQuestion8Text.text!!.isEmpty()
        ) {
            Toast.makeText(
                context,
                "Bitte gib in allen Textfeldern eine Frage ein",
                Toast.LENGTH_LONG
            ).show()
        } else {
            // Aufruf eines Dialogs zum Speichern eines Fragensatzes
            val dialog = Dialog(requireContext())
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_save_question_set)
            dialog.findViewById<Button>(R.id.btn_save).setOnClickListener {
                if (dialog.findViewById<EditText>(R.id.et_save_name).text.isEmpty()) {
                    Toast.makeText(context, "Bitte gib einen Namen ein", Toast.LENGTH_LONG).show()
                } else {
                    saveQuestionSet(dialog.findViewById<EditText>(R.id.et_save_name).text.toString())
                    Toast.makeText(context, "Fragen gespeichert", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
            dialog.findViewById<Button>(R.id.btn_break).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun saveQuestionSet(name: String) {
        val element: QuestionSetModel
        val date = getDate()
        element = QuestionSetModel(
            name = name,
            date = date,
            question1 = binding!!.tvQuestion1Text.text.toString(),
            question2 = binding!!.tvQuestion2Text.text.toString(),
            question3 = binding!!.tvQuestion3Text.text.toString(),
            question4 = binding!!.tvQuestion4Text.text.toString(),
            question5 = binding!!.tvQuestion5Text.text.toString(),
            question6 = binding!!.tvQuestion6Text.text.toString(),
            question7 = binding!!.tvQuestion7Text.text.toString(),
            question8 = binding!!.tvQuestion8Text.text.toString(),
            answer1 = binding!!.tvQuestion1SecondaryText.text.toString(),
            answer2 = binding!!.tvQuestion2SecondaryText.text.toString(),
            answer3 = binding!!.tvQuestion3SecondaryText.text.toString(),
            answer4 = binding!!.tvQuestion4SecondaryText.text.toString(),
            answer5 = binding!!.tvQuestion5SecondaryText.text.toString(),
            answer6 = binding!!.tvQuestion6SecondaryText.text.toString(),
            answer7 = binding!!.tvQuestion7SecondaryText.text.toString(),
            answer8 = binding!!.tvQuestion8SecondaryText.text.toString())
        questionSetBox.put(element)
    }

    private fun getDate(): String {
        val c = Calendar.getInstance()
        val dateTime = c.time
        val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
        return sdf.format(dateTime)
    }

    fun resetToDefault() {
        questionBox.removeAll()
        for (i in 0..7) {
            val question = QuestionsModel(
                question = sharedViewModel.getQuestions()[i],
                answer = sharedViewModel.getAnswers()[i])
            questionBox.put(question)
        }
        setQuestionListFromObjectBox()
    }

    private fun setQuestionListFromObjectBox() {
        val questions = questionBox.all
        binding!!.tvQuestion1Text.setText(questions[0].question)
        binding!!.tvQuestion1SecondaryText.setText(questions[0].answer)

        binding!!.tvQuestion2Text.setText(questions[1].question)
        binding!!.tvQuestion2SecondaryText.setText(questions[1].answer)

        binding!!.tvQuestion3Text.setText(questions[2].question)
        binding!!.tvQuestion3SecondaryText.setText(questions[2].answer)

        binding!!.tvQuestion4Text.setText(questions[3].question)
        binding!!.tvQuestion4SecondaryText.setText(questions[3].answer)

        binding!!.tvQuestion5Text.setText(questions[4].question)
        binding!!.tvQuestion5SecondaryText.setText(questions[4].answer)

        binding!!.tvQuestion6Text.setText(questions[5].question)
        binding!!.tvQuestion6SecondaryText.setText(questions[5].answer)

        binding!!.tvQuestion7Text.setText(questions[6].question)
        binding!!.tvQuestion7SecondaryText.setText(questions[6].answer)

        binding!!.tvQuestion8Text.setText(questions[7].question)
        binding!!.tvQuestion8SecondaryText.setText(questions[7].answer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}