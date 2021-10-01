package com.mfroemmi.gluecksradquizapp

import android.content.Context
import com.mfroemmi.gluecksradquizapp.model.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {

    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }

}