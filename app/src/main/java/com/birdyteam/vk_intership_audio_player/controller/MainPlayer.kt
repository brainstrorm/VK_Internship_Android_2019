package com.birdyteam.vk_intership_audio_player.controller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MotionEventCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.birdyteam.vk_intership_audio_player.R
import java.util.*

class MainPlayer : AnimatedFragment() {

    override var startAnimation: Boolean = false
    override lateinit var animation: Animation
    override lateinit var mView: View
    private lateinit var dropDownIcon : ImageView
    private lateinit var mainContainer : RelativeLayout
    private var delta = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_main_player, container, false)

        mainContainer = mView.findViewById<RelativeLayout>(R.id.MainContainer)

        dropDownIcon = mView.findViewById(R.id.DropDown)
        dropDownIcon.setOnTouchListener { view, motionEvent ->
            val y = motionEvent.rawY.toInt()

            when (motionEvent.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    val lParams = view.layoutParams as ConstraintLayout.LayoutParams
                    delta = y - lParams.topMargin
                    Log.d(ChooseFolderActivity.TAG, "ACTION_DOWN $delta")
                }
                MotionEvent.ACTION_UP -> {
                    val params = mainContainer.layoutParams as FrameLayout.LayoutParams
                    val display = activity?.windowManager?.defaultDisplay
                    val size = Point()
                    display?.getSize(size)
                    if(params.topMargin > (size.y / 4))
                        this@MainPlayer.replaceFragments(MiniPlayer())
                    else {
                        params.topMargin = 0
                        mainContainer.layoutParams = params
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val params = mainContainer.layoutParams as FrameLayout.LayoutParams
                    val saved = params.topMargin
                    params.topMargin = y - delta
                    mainContainer.layoutParams = params
                    Log.d(ChooseFolderActivity.TAG, "ACTION_MOVE $saved $delta")
                }
            }
            mView.invalidate()
            true
        }

        if(startAnimation)
            mView.startAnimation(animation)
        return mView
    }
}