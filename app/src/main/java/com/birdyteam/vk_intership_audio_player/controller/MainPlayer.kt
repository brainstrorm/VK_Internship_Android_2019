package com.birdyteam.vk_intership_audio_player.controller

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.birdyteam.vk_intership_audio_player.R

class MainPlayer : AnimatedFragment() {

    override var startAnimation: Boolean = false
    override lateinit var animation: Animation
    override lateinit var mView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_main_player, container, false)
        mView.setOnClickListener {
            (activity as? ChooseFolderActivity)?.swapFragments()
        }

        if(startAnimation)
            mView.startAnimation(animation)
        return mView
    }

    override fun replaceFragments(other: AnimatedFragment) {
        super.replaceFragments(other)

    }
}