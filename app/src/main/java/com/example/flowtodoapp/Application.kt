package com.example.flowtodoapp

import android.app.Application
import com.example.flowtodoapp.injection.appModule
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@FlowPreview
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(appModule)
        }
    }
}