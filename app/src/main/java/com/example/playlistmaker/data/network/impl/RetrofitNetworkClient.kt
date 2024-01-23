package com.example.playlistmaker.data.network.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.network.ITunesSearchAPI
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.Response
import com.example.playlistmaker.data.search.model.TrackRequest

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesSearchAPI
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        return if (dto is TrackRequest) {
            val resp = iTunesService.search(dto.expression).execute()
            val body = resp.body()?: Response()

            body.apply { resultCode = resp.code() }
        } else {
            Response().apply { resultCode = 400 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
