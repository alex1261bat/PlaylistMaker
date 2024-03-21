package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun openShareLink() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message))
            type = context.getString(R.string.share_intent_type)
        }
        val shareIntent = Intent
            .createChooser(intent, null)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun openEmail() {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(context.getString(R.string.mail_to))
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_theme))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_message))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    override fun openUserAgreement() {
        Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.user_agreement_data)))
            .apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    override fun sharePlaylist(name: String, description: String, tracks: List<Track>) {
        val message = createPlaylistInfoText(name, description, tracks)

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = context.getString(R.string.type_share_text)
        }
        val shareIntent = Intent
            .createChooser(sendIntent, null)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    private fun createPlaylistInfoText(
        name: String,
        description: String,
        tracks: List<Track>
    ): String {
        val tracksCountPluralEnding = context.resources.getQuantityString(
            R.plurals.track,
            tracks.count(),
            tracks.count()
        )
        val tracksCountText = "${tracks.count()} $tracksCountPluralEnding"
        val tracksInfoText = getTracksInfoText(tracks)
        return "$name\n$description\n$tracksCountText\n$tracksInfoText"
    }

    private fun getTracksInfoText(tracks: List<Track>): String {
        return tracks.mapIndexed { index: Int, track: Track ->
            "${index + 1}. " +
                    "${track.artistName} - " +
                    "${track.trackName} " +
                    "(${SimpleDateFormat("mm", Locale.getDefault()).format(track.trackTime.toLong())})\n"
        }.joinToString(separator = "")
    }
}
