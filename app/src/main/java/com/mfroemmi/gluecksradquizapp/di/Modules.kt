package com.mfroemmi.gluecksradquizapp.di

import com.mfroemmi.gluecksradquizapp.ObjectBox
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel
import com.mfroemmi.gluecksradquizapp.model.QuestionsModel
import io.objectbox.kotlin.boxFor
import org.koin.dsl.module.module

val applicationModul = module(override = true) {

    single ("questionsModel")  { ObjectBox.boxStore.boxFor<QuestionsModel>() }
    single ("questionSetModel")  { ObjectBox.boxStore.boxFor<QuestionSetModel>() }

}