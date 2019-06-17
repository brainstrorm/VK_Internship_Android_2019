package com.birdyteam.vk_intership_audio_player.model

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.birdyteam.vk_intership_audio_player.R
import com.birdyteam.vk_intership_audio_player.controller.ChooseFolderActivity

class MusicService : Service() {

    private val trackSingleton = TrackSingleton.getInstance()
    private var notificationManager : NotificationManager? = null

    companion object {

        private var mediaPlayer : MediaPlayer? = null

        const val SWAP_IMAGE = "swap"
        const val UPDATE_INFO = "update.info"

        const val TO_PAUSE = "pause"
        const val TO_PLAY = "play"

        private const val COMMAND = "command"
        private const val PLAY = "play"
        private const val STOP = "stop"
        private const val NEXT = "next"
        private const val CLEAR = "clear.player"
        private const val UPDATE_NOTIFICATION = "update.notification"

        private const val NOTIFICATION_ID = 1

        fun getInstance(context: Context, command : Command) : Intent {
            val mIntent = Intent(context, MusicService::class.java)
            when (command) {
                Command.PLAY_OR_STOP -> {
                    if(mediaPlayer == null || mediaPlayer?.isPlaying == false)
                        mIntent.putExtra(COMMAND, PLAY)
                    else
                        mIntent.putExtra(COMMAND, STOP)
                    Log.d(ChooseFolderActivity.TAG, "Play or Stop command sent")
                }
                Command.NEXT -> {
                    mIntent.putExtra(COMMAND, NEXT)
                }
                Command.CLEAR -> {
                    mIntent.putExtra(COMMAND, CLEAR)
                }
                Command.UPDATE_NOTIFICATION -> {
                    mIntent.putExtra(COMMAND, UPDATE_NOTIFICATION)
                }
                else -> {
                    Log.d(ChooseFolderActivity.TAG, "Didn't fetch")
                }
            }
            return mIntent
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification() = try {
        val track = trackSingleton.getCurrentTrack()
        NotificationCompat.Builder(this, ChooseFolderActivity.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(track.name)
            .setContentTitle(track.artist)
            .build()
    } catch (e : Exception) {
        NotificationCompat.Builder(this, ChooseFolderActivity.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(getString(R.string.undefined))
            .setContentTitle(getString(R.string.undefined))
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return super.onStartCommand(intent, flags, startId)
        Log.d(ChooseFolderActivity.TAG, "onStartCommand called with extra = ${intent.getStringExtra(COMMAND)}")
        when (intent.getStringExtra(COMMAND)) {
            PLAY -> {
                playMusic()
            }
            STOP -> {
                stopMusic()
            }
            NEXT -> {
                nextTrack(mediaPlayer?.isPlaying)
            }
            CLEAR -> {
                clearPlayer()
            }
            UPDATE_NOTIFICATION -> {
                updateNotification()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ChooseFolderActivity.TAG, "Service onDestroy() called")
        stopForeground(true)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun updateNotification() {
        val notification = createNotification()
        if(notificationManager == null)
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun clearPlayer() {
        mediaPlayer ?: return
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        notificationManager?.cancelAll()
    }

    private fun playMusic() {
        try {
            if(mediaPlayer == null) {
                createPlayer(trackSingleton.getCurrentTrack())
            }
            mediaPlayer?.start()
            if(mediaPlayer?.isPlaying == true) {
                sendSwapBroadCast(TO_PAUSE)
            }
            updateNotification()
            Log.d(ChooseFolderActivity.TAG, "Music has being started")
        } catch (e : Exception) {
            Log.d(ChooseFolderActivity.TAG,"Exception caught at StartPlaying $e")
        }
    }

    private fun stopMusic() {
        mediaPlayer ?: return
        mediaPlayer?.pause()
        if(mediaPlayer?.isPlaying == false) {
            sendSwapBroadCast(TO_PLAY)
        }
        Log.d(ChooseFolderActivity.TAG, "Music stopped")
    }

    private fun sendSwapBroadCast(extra : String) {
        val mIntent = Intent(SWAP_IMAGE)
        mIntent.putExtra(SWAP_IMAGE, extra)
        sendBroadcast(mIntent)
        Log.d(ChooseFolderActivity.TAG, "Broadcast sent with extra = $extra")
    }

    private fun nextTrack(startPlay : Boolean?) {
        Log.d(ChooseFolderActivity.TAG, "nextTrack called with startPlay = $startPlay")
        createPlayer(trackSingleton.nextTrack())
        sendBroadcast(Intent(UPDATE_INFO))
        if(startPlay == true)
            mediaPlayer?.start()
        updateNotification()
    }

    private fun createPlayer(track : Track) {
        if(mediaPlayer != null)
            mediaPlayer?.reset()
        else
            mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(this, track.uri)
        mediaPlayer?.prepare()
        mediaPlayer?.setOnCompletionListener {
            nextTrack(true)
        }
        Log.d(ChooseFolderActivity.TAG, "Player created with track :\n${track.uri}")
    }
}