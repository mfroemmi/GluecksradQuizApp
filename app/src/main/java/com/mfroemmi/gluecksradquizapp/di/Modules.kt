package com.mfroemmi.gluecksradquizapp.di

import com.mfroemmi.gluecksradquizapp.ObjectBox
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel
import com.mfroemmi.gluecksradquizapp.model.QuestionsModel
import com.mfroemmi.gluecksradquizapp.model.SettingsViewModel
import io.objectbox.kotlin.boxFor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationModul = module(override = true) {

    single (named("questionsModel"))  { ObjectBox.boxStore.boxFor<QuestionsModel>() }
    single (named("questionSetModel"))  { ObjectBox.boxStore.boxFor<QuestionSetModel>() }

    single { SettingsViewModel() }

}