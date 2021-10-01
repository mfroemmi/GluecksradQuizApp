package com.mfroemmi.gluecksradquizapp

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mfroemmi.gluecksradquizapp.databinding.FragmentQuestionListBinding
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel
import com.mfroemmi.gluecksradquizapp.model.QuestionsModel
import com.mfroemmi.gluecksradquizapp.model.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

class QuestionListFragment : Fragment() {

    private var binding: FragmentQuestionListBinding? = null
    private val sharedViewModel: SettingsViewModel by activityViewModels()

    private val questionBox = ObjectBox.boxStore.boxFor(QuestionsModel::class.java)
    private val questionSetBox = ObjectBox.boxStore.boxFor(QuestionSetModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        if (binding!!.etQuestion1.text.isEmpty() ||
            binding!!.etQuestion2.text.isEmpty() ||
            binding!!.etQuestion3.text.isEmpty() ||
            binding!!.etQuestion4.text.isEmpty() ||
            binding!!.etQuestion5.text.isEmpty() ||
            binding!!.etQuestion6.text.isEmpty() ||
            binding!!.etQuestion7.text.isEmpty() ||
            binding!!.etQuestion8.text.isEmpty()
        ) {
            Toast.makeText(
                context,
                "Bitte gib in allen Textfeldern eine Frage ein",
                Toast.LENGTH_LONG
            ).show()
        } else {
            questionBox.removeAll()
            questionBox.put(QuestionsModel(question = binding!!.etQuestion1.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.etQuestion2.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.etQuestion3.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.etQuestion4.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.etQuestion5.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.etQuestion6.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.etQuestion7.text.toString()))
            questionBox.put(QuestionsModel(question = binding!!.etQuestion8.text.toString()))

            findNavController().navigate(R.id.action_questionListFragment_to_settingsFragment)
        }

    }

    fun goToLoadQuestionSetFragment() {
        findNavController().navigate(R.id.action_questionListFragment_to_loadQuestionSetFragment)
    }

    fun goToSaveQuestionSetDialog() {
        if (binding!!.etQuestion1.text.isEmpty() ||
            binding!!.etQuestion2.text.isEmpty() ||
            binding!!.etQuestion3.text.isEmpty() ||
            binding!!.etQuestion4.text.isEmpty() ||
            binding!!.etQuestion5.text.isEmpty() ||
            binding!!.etQuestion6.text.isEmpty() ||
            binding!!.etQuestion7.text.isEmpty() ||
            binding!!.etQuestion8.text.isEmpty()
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

    fun saveQuestionSet(name: String) {
        var element: QuestionSetModel
        val date = getDate()
        element = QuestionSetModel(
            name = name,
            date = date,
            question1 = binding!!.etQuestion1.text.toString(),
            question2 = binding!!.etQuestion2.text.toString(),
            question3 = binding!!.etQuestion3.text.toString(),
            question4 = binding!!.etQuestion4.text.toString(),
            question5 = binding!!.etQuestion5.text.toString(),
            question6 = binding!!.etQuestion6.text.toString(),
            question7 = binding!!.etQuestion7.text.toString(),
            question8 = binding!!.etQuestion8.text.toString()
        )
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
            val question = QuestionsModel(question = sharedViewModel.getQuestions()[i])
            questionBox.put(question)
        }
        setQuestionListFromObjectBox()
    }

    private fun setQuestionListFromObjectBox() {
        val questions = questionBox.all
        binding!!.etQuestion1.setText(questions[0].question)
        binding!!.etQuestion2.setText(questions[1].question)
        binding!!.etQuestion3.setText(questions[2].question)
        binding!!.etQuestion4.setText(questions[3].question)
        binding!!.etQuestion5.setText(questions[4].question)
        binding!!.etQuestion6.setText(questions[5].question)
        binding!!.etQuestion7.setText(questions[6].question)
        binding!!.etQuestion8.setText(questions[7].question)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}