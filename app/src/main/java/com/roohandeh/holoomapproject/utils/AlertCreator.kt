package com.roohandeh.holoomapproject.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.roohandeh.holoomapproject.R
import java.lang.Exception

object AlertCreator {

    private var locationPermissionDialog: Dialog? = null

    fun showLocationPermissionDialog(context: Context) {
        if (locationPermissionDialog == null) {
            locationPermissionDialog = showSimpleDialog(
                context,
                R.string.turn_on_gps_title,
                R.string.access_location_permission_message,
                R.drawable.icon_gps_fixed,
                {
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:" + context.packageName)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                    }
                },
                null,
                R.string.activation
            )
        } else {
            locationPermissionDialog?.let { dialog ->
                if (dialog.isShowing.not()) {
                    dialog.show()
                }
            }
        }
    }

    private fun showSimpleDialog(
        context: Context,
        @StringRes title: Int? = null,
        @StringRes message: Int,
        @DrawableRes icon: Int,
        positiveButtonListener: ((DialogInterface) -> Unit)? = null,
        negativeButtonListener: ((DialogInterface) -> Unit)? = null,
        @StringRes positiveButtonText: Int? = null,
        @StringRes negativeButtonText: Int? = null
    ): Dialog {
        val dialog = AlertDialog.Builder(context)
        title?.let { title ->
            dialog.setTitle(title)
        }
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