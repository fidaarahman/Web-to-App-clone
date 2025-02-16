package com.threedev.appconvertor.app

import android.app.Application
import com.threedev.appconvertor.helpers.SessionManager

class AppController: Application() {

    override fun onCreate() {
        super.onCreate()
        SessionManager.with(this)
    }
}