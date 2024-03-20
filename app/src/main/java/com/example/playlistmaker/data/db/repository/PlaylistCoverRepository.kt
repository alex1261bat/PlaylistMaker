package com.example.playlistmaker.data.db.repository

import android.net.Uri

interface PlaylistCoverRepository {
    suspend fun savePlaylistCover(playlistId: String, uri: Uri): String
}
