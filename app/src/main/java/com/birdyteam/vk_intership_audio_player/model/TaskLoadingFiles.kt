package com.birdyteam.vk_intership_audio_player.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentManager
import com.birdyteam.vk_intership_audio_player.R
import com.birdyteam.vk_intership_audio_player.controller.ChooseFolderActivity
import com.birdyteam.vk_intership_audio_player.controller.MiniPlayer

@SuppressLint("StaticFieldLeak")
class TaskLoadingFiles(private val context: Context, private val fragmentManager: FragmentManager) :
    AsyncTask<DocumentFile, Track, Void>() {

    override fun doInBackground(vararg p0: DocumentFile?): Void? {
        Log.d(ChooseFolderActivity.TAG, "Doing background")
        p0[0]?.listFiles()
            ?.forEach {
                if (it.type == "audio/mpeg")
                    publishProgress(Track(context,it))
            }
        return null
    }

    override fun onProgressUpdate(vararg values: Track?) {
        super.onProgressUpdate(*values)
        Log.d(ChooseFolderActivity.TAG, "Task sent Track")
        TrackSingleton.getInstance().addTrack(values[0]!!)
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        val singleton = TrackSingleton.getInstance()
        Log.d(
            ChooseFolderActivity.TAG, singleton.size().toString() +
            " tracks in Singleton"
        )
        if(singleton.size() > 0) {
            val fragment = MiniPlayer()
            fragment.addFragmentAnimation(context, fragmentManager, View.VISIBLE)
            context.startService(
                MusicService.getInstance(context, Command.UPDATE_NOTIFICATION)
            )
        }
    }
}