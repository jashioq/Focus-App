package com.jan.focus

import android.app.Application
import di.KoinInitializer

class FocusApp : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}
