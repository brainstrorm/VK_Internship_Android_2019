package com.birdyteam.vk_intership_audio_player.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.documentfile.provider.DocumentFile

class Track(context: Context,file : DocumentFile) {

    var artist : String?
    var name : String?
    var albumImage : Bitmap? = null
    var duration : Long = 0
    var uri = file.uri
    var isPlayingNow = false

    init {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(context, uri)
        metadataRetriever.apply {
            artist = extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            name = extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            duration = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            val array = embeddedPicture
            if(array != null) {
                albumImage = BitmapFactory.decodeByteArray(
                    array,
                    0,
                    array.size
                )
            }
        }
    }
}