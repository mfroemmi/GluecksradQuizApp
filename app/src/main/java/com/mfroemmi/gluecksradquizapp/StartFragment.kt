package com.mfroemmi.gluecksradquizapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mfroemmi.gluecksradquizapp.databinding.FragmentStartBinding
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
class StartFragment : Fragment(), KoinComponent {

    private var binding: FragmentStartBinding? = null
    private val sharedViewModel: SettingsViewModel by viewModel()

    private val questionBox: Box<QuestionsModel> by inject(named("questionsModel"))
    private val questionSetBox: Box<QuestionSetModel> by inject(named("questionSetModel"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        /*
        if (questionSetBox.isEmpty) {
            loadQuestionSet()
        }
         */

        // Wird die App zum ersten mal gestartet, werden die "standard"-Fragen in die Datenbank geschrieben
        if (questionBox.isEmpty) {
            for (i in 0..7) {
                val question = QuestionsModel(
                    question = sharedViewModel.getQuestions()[i],
                    answer = sharedViewModel.getAnswers()[i])
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
    /*
    private fun loadQuestionSet() {
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
                question8 = "question8: $i",
                answer1 = "answer1: $i",
                answer2 = "answer2: $i",
                answer3 = "answer3: $i",
                answer4 = "answer4: $i",
                answer5 = "answer5: $i",
                answer6 = "answer6: $i",
                answer7 = "answer7: $i",
                answer8 = "answer8: $i")
            questionSetBox.put(element)
        }
    }
     */

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