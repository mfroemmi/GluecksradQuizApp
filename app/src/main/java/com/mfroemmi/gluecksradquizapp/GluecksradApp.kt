package com.mfroemmi.gluecksradquizapp

import android.app.Application
import com.mfroemmi.gluecksradquizapp.di.applicationModul
import org.koin.android.ext.android.startKoin

class GluecksradApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
        startKoin(this, listOf(applicationModul))
    }
}