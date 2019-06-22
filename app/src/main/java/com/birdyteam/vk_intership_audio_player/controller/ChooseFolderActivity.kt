package com.birdyteam.vk_intership_audio_player.controller

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
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

        private const val IS_GONE = "check.visibility"

        const val TAG = "Tag"
    }

    private var isGone = false
    private lateinit var chooseFolderBtn : Button
    private lateinit var boldText : TextView
    private lateinit var simpleText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_folder)
        supportActionBar?.hide()

        chooseFolderBtn = findViewById(R.id.choose_folder)
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

        boldText = findViewById(R.id.WhereFromText)
        simpleText = findViewById(R.id.ChooseFolderText)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean(IS_GONE, false))
                setInfoVisibility(View.GONE)
            else
                isGone = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_GONE, isGone)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.setSound(null, null)
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

    fun swapFragments() {
        val fm = supportFragmentManager
        when (val fragment = fm.findFragmentById(R.id.FragmentContainer)) {
            is MiniPlayer -> {
                setInfoVisibility(View.GONE)
                fragment.replaceFragments(MainPlayer())
            }
            is MainPlayer -> {
                fragment.replaceFragments(MiniPlayer())
            }
        }
    }

    fun setInfoVisibility(visibility : Int) {
        chooseFolderBtn.visibility = visibility
        boldText.visibility = visibility
        simpleText.visibility = visibility
        isGone = visibility == View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_PERMISSION) {
            if(grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                pickFolder()
            }
        }
    }
}
