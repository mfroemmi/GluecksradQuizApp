package com.mfroemmi.gluecksradquizapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mfroemmi.gluecksradquizapp.databinding.FragmentStartBinding
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel
import com.mfroemmi.gluecksradquizapp.model.QuestionsModel
import com.mfroemmi.gluecksradquizapp.model.SettingsViewModel
import io.objectbox.Box
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import java.text.SimpleDateFormat
import java.util.*

class StartFragment : Fragment(), KoinComponent {

    private var binding: FragmentStartBinding? = null
    private val sharedViewModel: SettingsViewModel by activityViewModels()

    private val questionBox: Box<QuestionsModel> by inject("questionsModel")
    private val questionSetBox: Box<QuestionSetModel> by inject("questionSetModel")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        if (questionSetBox.isEmpty) {
            loadQuestionSet()
        }

        // Wird die App zum ersten mal gestartet, werden die "standard"-Fragen in die Datenbank geschrieben
        if (questionBox.isEmpty) {
            for (i in 0..7) {
                val question = QuestionsModel(question = sharedViewModel.getQuestions()[i])
                questionBox.put(question)
            }
        }

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.startFragment = this
    }

    fun goToSpinFragment() {
        findNavController().navigate(R.id.action_startFragment_to_spinFragment)
    }

    fun goToSettingsFragment() {
        findNavController().navigate(R.id.action_startFragment_to_settingsFragment)
    }

    // TODO: Wird der Fragensatz-Platzhalter nicht mehr benötigt, kann die Funktion gelöscht werden
    fun loadQuestionSet() {
        var element: QuestionSetModel
        val date = getDate()
        for (i in 0..50) {
            element = QuestionSetModel(
                name = "Fragensatz $i",
                date = "($i) $date",
                question1 = "question1: $i",
                question2 = "question2: $i",
                question3 = "question3: $i",
                question4 = "question4: $i",
                question5 = "question5: $i",
                question6 = "question6: $i",
                question7 = "question7: $i",
                question8 = "question8: $i")
            questionSetBox.put(element)
        }
    }

    private fun getDate(): String {
        val c = Calendar.getInstance()
        val dateTime = c.time
        val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
        return sdf.format(dateTime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}