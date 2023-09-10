package com.roohandeh.holoomapproject.presentation.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.roohandeh.holoomapproject.R
import com.roohandeh.holoomapproject.domain.model.LocationAddress
import com.roohandeh.holoomapproject.utils.dp

@SuppressLint("ViewConstructor")
class CustomAddressView constructor(context: Context, private val locationAddress: LocationAddress) :
    RelativeLayout(context) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        addAvenueIcon(context)
        addAvenueText(context)
        addLineView(context)
        addAddressIcon(context)
        addAddressText(context)

    }

    private fun addAvenueIcon(context: Context) {
        val avenueIcon = ImageView(context)
        avenueIcon.id = R.id.img_avenue_icon
        avenueIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_street))
        val layoutParams = LayoutParams(30.dp, 30.dp)
        layoutParams.setMargins(4.dp, 0, 4.dp, 0)
        layoutParams.addRule(ALIGN_PARENT_TOP)
        layoutParams.addRule(ALIGN_PARENT_RIGHT)
        addView(avenueIcon, layoutParams)
    }

    private fun addAvenueText(context: Context) {
        val avenueText = TextView(context)
        avenueText.text = locationAddress.route
        avenueText.maxLines = 1
        avenueText.isSingleLine = true
        avenueText.ellipsize = TextUtils.TruncateAt.END
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(4.dp, 0, 4.dp, 2.dp)
        layoutParams.addRule(LEFT_OF, R.id.img_avenue_icon)
        layoutParams.addRule(ALIGN_BOTTOM, R.id.img_avenue_icon)
        addView(avenueText, layoutParams)
    }

    private fun addLineView(context: Context) {
        val lineView = View(context)
        lineView.id = R.id.address_line_view
        lineView.setBackgroundColor(Color.DKGRAY)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1.dp)
        layoutParams.addRule(BELOW, R.id.img_avenue_icon)
        layoutParams.setMargins(8.dp, 8.dp, 8.dp, 8.dp)
        addView(lineView, layoutParams)
    }

    private fun addAddressIcon(context: Context) {
        val addressIcon = ImageView(context)
        addressIcon.id = R.id.img_address_icon
        addressIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_location))
        val layoutParams = LayoutParams(30.dp, 30.dp)
        layoutParams.setMargins(4.dp, 0, 4.dp, 0)
        layoutParams.addRule(BELOW, R.id.address_line_view)
        layoutParams.addRule(ALIGN_PARENT_RIGHT)
        addView(addressIcon, layoutParams)
    }

    private fun addAddressText(context: Context) {
        val addressText = TextView(context)
        addressText.text = locationAddress.address
        addressText.maxLines = 1
        addressText.isSingleLine = true
        addressText.ellipsize = TextUtils.TruncateAt.END
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(4.dp, 0, 4.dp, 2.dp)
        layoutParams.addRule(LEFT_OF, R.id.img_address_icon)
        layoutParams.addRule(ALIGN_BOTTOM, R.id.img_address_icon)
        addView(addressText, layoutParams)
    }

}