package com.birdyteam.vk_intership_audio_player.controller

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.birdyteam.vk_intership_audio_player.R
import com.birdyteam.vk_intership_audio_player.model.Command
import com.birdyteam.vk_intership_audio_player.model.MusicService
import com.birdyteam.vk_intership_audio_player.model.TaskLoadingFiles
import com.birdyteam.vk_intership_audio_player.model.TrackSingleton

class ChooseFolderActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_GET_FOLDER = 0
        private const val REQUEST_PERMISSION = 1

        const val CHANNEL_ID = "main.channel"
        private const val CHANNEL_NAME = "MP3 Player"

        const val TAG = "Tag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_folder)
        supportActionBar?.hide()

        val chooseFolderBtn = findViewById<Button>(R.id.choose_folder)
        chooseFolderBtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this@ChooseFolderActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ChooseFolderActivity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION
                )
            } else
                pickFolder()
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun pickFolder() {
        val mIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        mIntent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(mIntent,
            REQUEST_GET_FOLDER
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_GET_FOLDER) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                TrackSingleton.getInstance().clear()
                val uri = data.data
                val file = DocumentFile.fromTreeUri(this, uri!!)
                TaskLoadingFiles(
                    applicationContext,
                    supportFragmentManager
                ).execute(file)
                cleanScreen()
                Log.d(TAG, "Started AsyncTask")
            }
        }
    }

    private fun cleanScreen() {
        startService(
            MusicService.getInstance(this, Command.CLEAR)
        )
        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.FragmentContainer)
        if(fragment != null && fragment is MiniPlayer) {
            fm.beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_PERMISSION) {
            if(grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                pickFolder()
            }
        }
    }
}
