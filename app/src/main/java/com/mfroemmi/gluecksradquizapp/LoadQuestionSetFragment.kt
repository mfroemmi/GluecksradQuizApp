package com.mfroemmi.gluecksradquizapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.mfroemmi.gluecksradquizapp.adapter.QuestionSetAdapter
import com.mfroemmi.gluecksradquizapp.databinding.FragmentLoadQuestionSetBinding
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel
import com.mfroemmi.gluecksradquizapp.model.QuestionsModel

class LoadQuestionSetFragment : Fragment() {

    // questionSetLiveData und loadOrDeleteLiveData wird im Recyclerview auf Änderungen überwacht
    private var questionSetLiveData = MutableLiveData<QuestionSetModel>()
    private var loadOrDeleteLiveData = MutableLiveData<String>()

    private var binding: FragmentLoadQuestionSetBinding? = null

    private val questionBox = ObjectBox.boxStore.boxFor(QuestionsModel::class.java)
    private val questionSetBox = ObjectBox.boxStore.boxFor(QuestionSetModel::class.java)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentLoadQuestionSetBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        // Initialisierung der RecyclerView und des Adapters
        val questionSetArrayList = convertObjectBoxToArrayList()
        val recyclerView = binding!!.recyclerView
        val adapter = QuestionSetAdapter(requireContext(), questionSetArrayList, questionSetLiveData, loadOrDeleteLiveData)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        // questionSet wird im Recyclerview auf Änderungen überwacht
        questionSetLiveData.observe(viewLifecycleOwner, {
            println(questionSetLiveData.value)
            if (loadOrDeleteLiveData.value == "load") {
                questionSetLiveData.value?.let { it -> loadSelectedQuestionSet(it) }
                goToQuestionListFragment()
            }
            if (loadOrDeleteLiveData.value == "delete") {
                questionSetLiveData.value?.let { it -> deleteSelectedQuestionSet(it) }
                questionSetArrayList.clear()
                questionSetArrayList.addAll(convertObjectBoxToArrayList())
                adapter.notifyDataSetChanged()
            }
        })

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loadQuestionSetFragment = this
    }

    fun goToQuestionListFragment() {
        findNavController().navigate(R.id.action_loadQuestionSetFragment_to_questionListFragment)
    }

    private fun convertObjectBoxToArrayList(): ArrayList<QuestionSetModel> {
        val list = ArrayList<QuestionSetModel>()
        for (i in 0 until questionSetBox.all.size) {
            list.add(questionSetBox.all[i])
        }
        return list
    }

    // Wird ein neuer Fragensatz geladen wird dieser in der ObjectBox gespeichert
    private fun loadSelectedQuestionSet(questionSet: QuestionSetModel) {
        questionBox.removeAll()
        questionBox.put(QuestionsModel(question = questionSet.question1))
        questionBox.put(QuestionsModel(question = questionSet.question2))
        questionBox.put(QuestionsModel(question = questionSet.question3))
        questionBox.put(QuestionsModel(question = questionSet.question4))
        questionBox.put(QuestionsModel(question = questionSet.question5))
        questionBox.put(QuestionsModel(question = questionSet.question6))
        questionBox.put(QuestionsModel(question = questionSet.question7))
        questionBox.put(QuestionsModel(question = questionSet.question8))
    }

    // Wird ein Fragensatz im ViewPager gelöscht, wird dieser in der ObjectBox entfernt
    private fun deleteSelectedQuestionSet(questionSet: QuestionSetModel) {
        questionSetBox.remove(questionSet)
    }

}