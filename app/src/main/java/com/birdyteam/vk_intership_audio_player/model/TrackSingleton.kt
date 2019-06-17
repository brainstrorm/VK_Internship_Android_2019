package com.birdyteam.vk_intership_audio_player.model

import java.lang.IllegalArgumentException

class TrackSingleton private constructor(){
    companion object {

        private var trackSingleton : TrackSingleton? = null

        fun getInstance() : TrackSingleton {
            if(trackSingleton == null) {
                trackSingleton = TrackSingleton()
            }
            return trackSingleton!!
        }
    }

    private var trackList : List<Track> = ArrayList()
    private var index = 0

    fun addTrack(track: Track) {
        trackList = trackList + track
    }

    fun clear() {
        trackList = ArrayList()
        index = 0
    }

    fun nextTrack() : Track {
        index = ((index + 1) % trackList.size)
        return trackList[index]
    }

    fun prevTrack() : Track {
        if(index == 0) {
            index = trackList.size - 1
        } else
            index -= 1
        return trackList[index]
    }

    fun getCurrentTrack() : Track {
        if(trackList.isEmpty())
            throw IllegalArgumentException("trackList is empty")
        return trackList[index]
    }

    fun size() = trackList.size

}