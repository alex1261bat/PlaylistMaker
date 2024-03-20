package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.FavoriteTracksEntity
import com.example.playlistmaker.data.db.entity.PlaylistTracksEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.ListConverter

class PlaylistMakerDbConverter {

    fun mapToFavoriteTracksEntity(track: Track, createdAt: Long = 0L): FavoriteTracksEntity {
        return FavoriteTracksEntity(
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

    fun mapToTrack(favoriteTracksEntity: FavoriteTracksEntity): Track {
        return Track(
            trackId = favoriteTracksEntity.id,
            trackName = favoriteTracksEntity.trackName,
            artistName = favoriteTracksEntity.artistName,
            trackTime = favoriteTracksEntity.trackTime,
            artworkUrl100 = favoriteTracksEntity.artworkUrl100,
            collectionName = favoriteTracksEntity.collectionName,
            releaseDate = favoriteTracksEntity.releaseDate,
            primaryGenreName = favoriteTracksEntity.primaryGenreName,
            country = favoriteTracksEntity.country,
            previewUrl = favoriteTracksEntity.previewUrl,
            isFavorite = true
        )
    }

    fun mapToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            playlistCoverUri = playlist.playlistCoverUri,
            tracksIds = playlist.tracksIds,
            tracksCount = playlist.tracksCount
        )
    }

    fun mapToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            title = playlistEntity.title,
            description = playlistEntity.description,
            playlistCoverUri = playlistEntity.playlistCoverUri,
            tracksIds = playlistEntity.tracksIds,
            tracksCount = playlistEntity.tracksCount
        )
    }

    fun mapToPlaylistTracksEntity(track: Track): PlaylistTracksEntity {
        return PlaylistTracksEntity(
            id = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
