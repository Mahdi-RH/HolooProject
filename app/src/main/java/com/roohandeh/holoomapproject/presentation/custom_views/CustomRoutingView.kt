package com.roohandeh.holoomapproject.presentation.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.roohandeh.holoomapproject.R
import com.roohandeh.holoomapproject.domain.model.LocationAddress
import com.roohandeh.holoomapproject.utils.dp

@SuppressLint("ViewConstructor")
class CustomRoutingView constructor(
    context: Context,
    private val locationAddress: LocationAddress
) : RelativeLayout(context) {

    lateinit var routingButton: AppCompatButton
    lateinit var closeButton: ImageView

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setPadding(12.dp, 12.dp, 12.dp, 12.dp)
        createCloseButton(context)
        createAddressView(context)
        createRoutingButton(context)
        createSaveButton(context)
        createSavedListButton(context)
    }

    private fun createCloseButton(context: Context) {
        closeButton = ImageView(context)
        closeButton.id = R.id.btn_close
        closeButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_close))
        val layoutParams = LayoutParams(22.dp, 22.dp)
        layoutParams.setMargins(2.dp, 2.dp, 2.dp, 2.dp)
        layoutParams.addRule(ALIGN_PARENT_TOP )
        layoutParams.addRule(ALIGN_PARENT_LEFT )
        addView(closeButton, layoutParams)
    }

    private fun createAddressView(context: Context) {
        val addressView = CustomAddressView(context, locationAddress)
        addressView.id = R.id.address_view
        addView(addressView)
    }

    private fun createRoutingButton(context: Context) {
         routingButton = AppCompatButton(context)
        routingButton.id = R.id.btn_routing
        routingButton.text=resources.getString(R.string.routing)
        routingButton.setPadding(4.dp, 4.dp, 4.dp, 4.dp)
        val layoutParams = LayoutParams(90.dp, 34.dp)
        layoutParams.setMargins(4.dp, 10.dp, 10.dp, 4.dp)
        layoutParams.addRule(BELOW, R.id.address_view)
        layoutParams.addRule(ALIGN_PARENT_RIGHT)
        routingButton.background = ContextCompat.getDrawable(context, R.drawable.routing_buttons_selector)
        addView(routingButton, layoutParams)
    }

    private fun createSaveButton(context: Context) {
        val button = AppCompatButton(context)
        button.id = R.id.btn_save
        button.text = resources.getString(R.string.save)
        button.setPadding(4.dp, 4.dp, 4.dp, 4.dp)
        val layoutParams = LayoutParams(90.dp, 34.dp)
        layoutParams.setMargins(4.dp, 10.dp, 4.dp, 4.dp)
        layoutParams.addRule(BELOW, R.id.address_view)
        layoutParams.addRule(LEFT_OF, R.id.btn_routing)
        button.background = ContextCompat.getDrawable(context, R.drawable.routing_buttons_selector)
        addView(button, layoutParams)
    }

    private fun createSavedListButton(context: Context) {
        val button = AppCompatButton(context)
        button.id = R.id.btn_saved_list
        button.text=resources.getString(R.string.saved_places)
        button.setPadding(4.dp, 4.dp, 4.dp, 4.dp)
        val layoutParams = LayoutParams(90.dp, 34.dp)
        layoutParams.setMargins(4.dp, 10.dp, 4.dp, 4.dp)
        layoutParams.addRule(BELOW, R.id.address_view)
        layoutParams.addRule(LEFT_OF, R.id.btn_save)
        button.background = ContextCompat.getDrawable(context, R.drawable.routing_buttons_selector)
        addView(button, layoutParams)
    }

}