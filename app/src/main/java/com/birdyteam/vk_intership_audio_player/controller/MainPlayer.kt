package com.birdyteam.vk_intership_audio_player.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.birdyteam.vk_intership_audio_player.R

class MainPlayer : Fragment() {

    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_main_player, container, false)
        mView.setOnClickListener {
            (activity as? ChooseFolderActivity)?.swapFragments()
        }
        return mView
    }
}