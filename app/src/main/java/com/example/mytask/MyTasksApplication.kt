package com.example.mytask

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyTasksApplication: Application()  {
    lateinit var context:Context
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Timber.plant(Timber.DebugTree())
    }
    fun getAppContext(): Context? {
        return context
    }
}