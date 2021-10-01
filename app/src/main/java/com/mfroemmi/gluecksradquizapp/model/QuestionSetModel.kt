package com.mfroemmi.gluecksradquizapp.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class QuestionSetModel (

    @Id
    var id: Long = 0,
    val name: String,
    val date: String,
    val question1: String,
    val question2: String,
    val question3: String,
    val question4: String,
    val question5: String,
    val question6: String,
    val question7: String,
    val question8: String

        )