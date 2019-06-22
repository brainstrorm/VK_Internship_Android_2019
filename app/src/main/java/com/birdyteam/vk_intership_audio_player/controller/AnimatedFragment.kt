package com.birdyteam.vk_intership_audio_player.controller

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.birdyteam.vk_intership_audio_player.R

abstract class AnimatedFragment : Fragment() {
    abstract var startAnimation : Boolean
    abstract var animation : Animation
    abstract var mView : View

    fun addFragmentAnimation(context : Context, fm : FragmentManager, visibility : Int) {
        val mAnimation = AnimationUtils.loadAnimation(context, R.anim.appear)
        mAnimation.duration = 500
        mAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                (activity as? ChooseFolderActivity)?.setInfoVisibility(visibility)
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })
        fm.beginTransaction()
            .add(R.id.FragmentContainer, this)
            .commit()
        animation = mAnimation
        startAnimation = true
    }

    open fun replaceFragments(other : AnimatedFragment) {
        val mActivity = activity
        mActivity ?: return

        val animation = AnimationUtils.loadAnimation(mActivity, R.anim.hide)
        animation.duration = 500
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                mActivity.supportFragmentManager.beginTransaction()
                    .remove(this@AnimatedFragment)
                    .commit()
                other.addFragmentAnimation(
                    mActivity,
                    mActivity.supportFragmentManager,
                    if(this@AnimatedFragment is MainPlayer)
                        View.VISIBLE
                    else
                        View.GONE
                )
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })
        mView.startAnimation(animation)
    }
}