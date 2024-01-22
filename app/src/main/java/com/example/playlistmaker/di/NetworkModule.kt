package com.example.playlistmaker.di

import com.example.playlistmaker.data.network.ITunesSearchAPI
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.impl.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ITUNES_BASE_URL = "https://itunes.apple.com/"

val networkModule = module {

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ITunesSearchAPI> {
        get<Retrofit>().create(ITunesSearchAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            context = androidContext(),
            iTunesService = get()
        )
    }
}
