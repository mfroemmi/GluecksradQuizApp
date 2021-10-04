package com.mfroemmi.gluecksradquizapp

import android.app.Application
import com.mfroemmi.gluecksradquizapp.di.applicationModul
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GluecksradApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
        startKoin{
            androidLogger()
            androidContext(this@GluecksradApp)
            modules(applicationModul)
        }
    }
}