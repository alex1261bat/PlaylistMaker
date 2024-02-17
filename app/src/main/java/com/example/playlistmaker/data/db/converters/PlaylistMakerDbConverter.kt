package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.domain.model.Track

class PlaylistMakerDbConverter {

    fun mapToTrackEntity(track: Track, createdAt: Long = 0L): TrackEntity {
        return TrackEntity(
            id = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            createdAt = createdAt
        )
    }

    fun mapToTrack(trackEntity: TrackEntity): Track {
        return Track(
            trackId = trackEntity.id,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTime = trackEntity.trackTime,
            artworkUrl100 = trackEntity.artworkUrl100,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl,
            isFavorite = true
        )
    }
}
