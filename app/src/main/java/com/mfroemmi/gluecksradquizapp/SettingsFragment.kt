package com.mfroemmi.gluecksradquizapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mfroemmi.gluecksradquizapp.databinding.FragmentSettingsBinding
import com.mfroemmi.gluecksradquizapp.model.SettingsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

@KoinApiExtension
class SettingsFragment : Fragment(), KoinComponent {

    private var binding: FragmentSettingsBinding? = null
    private val sharedViewModel: SettingsViewModel by viewModel()

    private var mTryLeftover: Int? = null
    private var mIntensity: Int? = null
    private var mMode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        // Initialisierung
        mTryLeftover = sharedViewModel.tryLeftover.value
        mIntensity = sharedViewModel.intensity.value
        mMode = sharedViewModel.mode.value

        if (mMode == "score") {
            setScoreMode()
        } else {
            setQuestionMode()
        }

        binding!!.tvQuestions.setOnClickListener {
            // Ändert die Buttons im Menü Spielmodus
            mMode = "question"
            sharedViewModel.setMode(mMode!!)
            setQuestionMode()
        }

        binding!!.tvScore.setOnClickListener {
            // Ändert die Buttons im Menü Spielmodus
            mMode = "score"
            sharedViewModel.setMode(mMode!!)
            setScoreMode()
        }

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewModel = sharedViewModel
            settingsFragment = this@SettingsFragment
        }

        sharedViewModel.tryLeftover.observe(viewLifecycleOwner, {binding!!.tvTries.text = sharedViewModel.tryLeftover.value.toString()})
        sharedViewModel.tryLeftover.observe(viewLifecycleOwner, {
            if (it == 1)
            binding!!.rbTries1.isChecked = true
        })
        sharedViewModel.intensity.observe(viewLifecycleOwner, {binding!!.tvIntensity.text = sharedViewModel.intensity.value.toString()})
    }

    fun goToStartFragment() {
        findNavController().navigate(R.id.action_settingsFragment_to_startFragment)
    }

    fun goToQuestionListFragment() {
        findNavController().navigate(R.id.action_settingsFragment_to_questionListFragment)
    }

    private fun setQuestionMode() {
        // Änderungen im Auswahlfenster des Modus
        binding!!.tvQuestions.setBackgroundResource(R.drawable.button_background)
        binding!!.tvQuestions.setTextColor(resources.getColor(R.color.white))
        binding!!.tvScore.setBackgroundResource(R.drawable.round_purple_light_background)
        binding!!.tvScore.setTextColor(resources.getColor(R.color.black))

        // Änderungen im Auswahlfenster der Optionen
        binding!!.rbTries1.isClickable = true
        binding!!.rbTries2.isClickable = false
        binding!!.rbTries3.isClickable = false

        binding!!.rbTries1.visibility = View.VISIBLE
        binding!!.rbTries2.visibility = View.INVISIBLE
        binding!!.rbTries3.visibility = View.INVISIBLE

        sharedViewModel.setTryLeftover(1)
    }

    private fun setScoreMode() {
        // Änderungen im Auswahlfenster des Modus
        binding!!.tvQuestions.setBackgroundResource(R.drawable.round_purple_light_background)
        binding!!.tvQuestions.setTextColor(resources.getColor(R.color.black))
        binding!!.tvScore.setBackgroundResource(R.drawable.button_background)
        binding!!.tvScore.setTextColor(resources.getColor(R.color.white))

        // Änderungen im Auswahlfenster der Optionen
        binding!!.rbTries1.isClickable = true
        binding!!.rbTries2.isClickable = true
        binding!!.rbTries3.isClickable = true

        binding!!.rbTries1.visibility = View.VISIBLE
        binding!!.rbTries2.visibility = View.VISIBLE
        binding!!.rbTries3.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}