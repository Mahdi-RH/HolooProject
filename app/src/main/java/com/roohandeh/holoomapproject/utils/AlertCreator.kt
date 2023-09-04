package com.roohandeh.holoomapproject.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.roohandeh.holoomapproject.R

object AlertCreator {

    private var internetActivationDialog: Dialog? = null
    private var gpsActivationDialog: Dialog? = null

    fun showInternetActivationDialog(context: Context) {
        if (internetActivationDialog == null) {
            internetActivationDialog = showSimpleDialog(
                context,
                R.string.turn_on_internet_title,
                R.string.turn_on_internet_message,
                R.drawable.icon_wifi_data,
                {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    it.dismiss()
                }, {
                    context.startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                    it.dismiss()
                }, R.string.wifi_internet,
                R.string.data_internet
            )
        } else {
            internetActivationDialog?.show()
        }
    }

    fun dismissInternetActivationDialog() {
        internetActivationDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    fun showGpsActivationDialog(context: Context) {
        if (gpsActivationDialog == null) {
            gpsActivationDialog = showSimpleDialog(
                context,
                R.string.turn_on_gps_title,
                R.string.turn_on_gps_message,
                R.drawable.icon_gps_fixed,
                {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    it.dismiss()
                }, {

                }, R.string.activation
            )
        } else {
            gpsActivationDialog?.show()
        }
    }

    fun dismissGpsActivationDialog() {
        gpsActivationDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun showSimpleDialog(
        context: Context,
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes icon: Int,
        positiveButtonListener: ((DialogInterface) -> Unit)? = null,
        negativeButtonListener: ((DialogInterface) -> Unit)? = null,
        @StringRes positiveButtonText: Int? = null,
        @StringRes negativeButtonText: Int? = null
    ): Dialog {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setIcon(icon)
        dialog.setMessage(message)
        dialog.setCancelable(false)
        positiveButtonText?.let { text ->
            positiveButtonListener?.let {
                dialog.setPositiveButton(context.resources.getString(text)) { dialogInterface, _ ->
                    positiveButtonListener(dialogInterface)
                }
            }
        }
        negativeButtonText?.let { text ->
            negativeButtonListener?.let {
                dialog.setNegativeButton(context.resources.getString(text)) { dialogInterface, _ ->
                    negativeButtonListener(dialogInterface)
                }
            }
        }

        return dialog.show()
    }

}