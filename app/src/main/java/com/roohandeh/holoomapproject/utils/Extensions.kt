package com.roohandeh.holoomapproject.utils

import android.content.res.Resources
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Fragment.showToast(message:String){
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun View.slideUp() {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(
        0f,
        0f,
        height.toFloat(),
        0f
    )
    animate.duration = 300
    animate.fillAfter = true
    startAnimation(animate)
}

fun View.slideDown() {
    val animate = TranslateAnimation(
        0f,
        0f,
        0f,
        height.toFloat()
    )
    animate.duration = 300
    animate.fillAfter = true
    startAnimation(animate)
}

fun View.fadeIn() {
    visibility = View.VISIBLE
    animate().alpha(1f).duration = 300
}
fun View.fadeOut() {
    visibility = View.GONE
    animate().alpha(0f).duration = 300
}
