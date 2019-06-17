package com.birdyteam.vk_intership_audio_player.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.birdyteam.vk_intership_audio_player.R
import com.birdyteam.vk_intership_audio_player.model.Command
import com.birdyteam.vk_intership_audio_player.model.MusicService
import com.birdyteam.vk_intership_audio_player.model.Track
import com.birdyteam.vk_intership_audio_player.model.TrackSingleton
import java.lang.Exception

class MiniPlayer : Fragment() {

    companion object {
        private const val SAVE_PLAYING = "save.playing.state"
    }

    private var isPlaying = false
    private lateinit var playBtn : ImageButton
    private lateinit var nextBtn : ImageButton
    private lateinit var albumImage : ImageView
    private lateinit var trackName : TextView
    private lateinit var duration : TextView
    private lateinit var mView : View
    private lateinit var track: Track
    private val receiverForSwapPause = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1 ?: return
            when (p1.getStringExtra(MusicService.SWAP_IMAGE)) {
                MusicService.TO_PAUSE -> {
                    isPlaying = true
                    playBtn.setImageResource(R.drawable.ic_pause_28)
                }
                MusicService.TO_PLAY -> {
                    isPlaying = false
                    playBtn.setImageResource(R.drawable.ic_play_48)
                }
            }
        }
    }
    private val receiverForUpdateInfo = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1 ?: return
            updateInfoOnView()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVE_PLAYING, isPlaying)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_mini_player, container, false)
        playBtn = mView.findViewById(R.id.MiniStopBtn)
        playBtn.setOnClickListener {
            activity?.startService(MusicService.getInstance(activity!!, Command.PLAY_OR_STOP))
        }

        if (savedInstanceState != null) {
            isPlaying = savedInstanceState.getBoolean(SAVE_PLAYING, false)
        }
        if (isPlaying)
            playBtn.setImageResource(R.drawable.ic_pause_28)

        nextBtn = mView.findViewById(R.id.MiniNextBtn)
        nextBtn.setOnClickListener {
            activity?.startService(
                MusicService.getInstance(activity!!, Command.NEXT)
            )
        }

        duration = mView.findViewById(R.id.TrackDuration)

        albumImage = mView.findViewById(R.id.AlbumImage)

        trackName = mView.findViewById(R.id.TrackName)

        activity?.registerReceiver(receiverForSwapPause, IntentFilter(MusicService.SWAP_IMAGE))
        activity?.registerReceiver(receiverForUpdateInfo, IntentFilter(MusicService.UPDATE_INFO))

        mView.setOnClickListener {
            (activity as? ChooseFolderActivity)?.swapFragments()
        }

        updateInfoOnView()
        return mView
    }

    private fun updateInfoOnView() {
        try {
            track = TrackSingleton.getInstance().getCurrentTrack()
            if(track.albumImage != null)
                albumImage.setImageBitmap(track.albumImage)
            else
                albumImage.setImageResource(R.drawable.unknown_album)
            if(track.name == null)
                trackName.text = getString(R.string.undefined)
            else
                trackName.text = track.name
            trackName.isSelected = true

            duration.text = getString(R.string.time_pattern,
                (track.duration / 60000),
                (track.duration / 10000) % 6,
                (track.duration / 1000) % 10
            )
        } catch (e : Exception) {
            Log.d(ChooseFolderActivity.TAG, "TrackList is Empty")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(receiverForSwapPause)
        activity?.unregisterReceiver(receiverForUpdateInfo)
    }
}