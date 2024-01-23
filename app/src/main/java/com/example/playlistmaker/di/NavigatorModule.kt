package com.example.playlistmaker.di

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val navigatorModule = module {

    single<ExternalNavigator> {
        ExternalNavigatorImpl(context = androidContext())
    }
}
