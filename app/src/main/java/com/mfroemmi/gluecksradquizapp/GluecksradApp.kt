package com.mfroemmi.gluecksradquizapp

import android.app.Application

class GluecksradApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
    }
}