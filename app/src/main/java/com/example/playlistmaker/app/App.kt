package com.example.playlistmaker.app

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.navigatorModule
import com.example.playlistmaker.di.networkModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.sharedPreferencesModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                interactorModule,
                navigatorModule,
                networkModule,
                repositoryModule,
                sharedPreferencesModule,
                viewModelModule
            )
        }
    }
}
