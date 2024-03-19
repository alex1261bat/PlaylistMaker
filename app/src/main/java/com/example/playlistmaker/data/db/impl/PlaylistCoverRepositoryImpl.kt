package com.example.playlistmaker.data.db.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.db.repository.PlaylistCoverRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistCoverRepositoryImpl(private val context: Context) : PlaylistCoverRepository {
    companion object {
        const val DIRECTORY_NAME = "playlist_maker"
        const val FILE_NAME = "playlist_cover"
        const val FILE_EXTENSION = ".jpg"
        const val COMPRESS_QUALITY = 30
    }

    override suspend fun savePlaylistCover(playlistId: String, uri: Uri): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            DIRECTORY_NAME)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "$FILE_NAME$playlistId$FILE_EXTENSION")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, outputStream)

        withContext(Dispatchers.IO) {
            inputStream?.close()
        }
        withContext(Dispatchers.IO) {
            outputStream.close()
        }

        return file.toString()
    }
}
