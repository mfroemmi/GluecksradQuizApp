package com.mfroemmi.gluecksradquizapp.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class QuestionsModel(

    @Id
    var id: Long = 0,
    val question: String

)

