package com.roohandeh.holoomapproject.presentation.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.roohandeh.holoomapproject.R
import com.roohandeh.holoomapproject.databinding.ActivityBaseBinding
import com.roohandeh.holoomapproject.utils.NetworkCallBack

/**
 *  A parent for every activity that want to implement viewBinding.
 *
 */
abstract class
BaseBindingActivity<out VB : ViewBinding> : AppCompatActivity(),NetworkCallBack {

    private lateinit var _binding: ViewBinding
    abstract val bindingInflater: (LayoutInflater) -> VB
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB
    private lateinit var baseViewBinding: ActivityBaseBinding
    private var internetActivationDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewBinding = ActivityBaseBinding.inflate(layoutInflater)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(_binding.root)

    }

    /**
     *  BaseContainer will hold view of subclasses
     */
    override fun setContentView(layoutResID: Int) {
        super.setContentView(baseViewBinding.root)
        baseViewBinding.containerBase.removeAllViews()
        baseViewBinding.containerBase.addView(layoutInflater.inflate(layoutResID, null))
        initView()
    }

    override fun setContentView(view: View?) {
        super.setContentView(baseViewBinding.root)
        baseViewBinding.containerBase.removeAllViews()
        baseViewBinding.containerBase.addView(view)
        initView()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(baseViewBinding.root, params)
        baseViewBinding.containerBase.removeAllViews()
        baseViewBinding.containerBase.addView(view)
        initView()
    }

    abstract fun initView()

    fun checkGpsAndInternetAvailability() {
        if (isGpsEnabled().not()) {
            showGpsActivationDialog()
            return
        }
        if (isNetworkAvailable().not()) {
            internetActivationDialog = showInternetActivationDialog()
            return
        }
    }

     private fun showInternetActivationDialog():Dialog {
        return showSimpleDialog(
            R.string.turn_on_internet_title,
            R.string.turn_on_internet_message,
            R.drawable.icon_wifi_data,
            {
                startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS))
                it.dismiss()
            }, {
                startActivity(Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS))
                it.dismiss()
            }, R.string.wifi_internet,
            R.string.data_internet
        )
    }

    private fun showGpsActivationDialog():Dialog {
        return showSimpleDialog(
            R.string.turn_on_gps_title,
            R.string.turn_on_gps_message,
            R.drawable.icon_gps_fixed,
            {
                startActivity( Intent (android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                it.dismiss()
            }, {

            }, R.string.activation
        )
    }

    private fun isGpsEnabled() =
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )

    @RequiresApi(Build.VERSION_CODES.N)
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onAvailableNetwork(network)
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
    }

    private fun showSimpleDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes icon: Int,
        positiveButtonListener: ((DialogInterface) -> Unit)? = null,
        negativeButtonListener: ((DialogInterface) -> Unit)? = null,
        @StringRes positiveButtonText: Int? = null,
        @StringRes negativeButtonText: Int? = null
    ) :Dialog{
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
        dialog.setIcon(icon)
        dialog.setMessage(message)
        dialog.setCancelable(false)
        positiveButtonText?.let { text ->
            positiveButtonListener?.let {
                dialog.setPositiveButton(resources.getString(text)) { dialogInterface, _ ->
                    positiveButtonListener(dialogInterface)
                }
            }
        }
        negativeButtonText?.let { text ->
            negativeButtonListener?.let {
                dialog.setNegativeButton(resources.getString(text)) { dialogInterface, _ ->
                    negativeButtonListener(dialogInterface)
                }
            }
        }
        return dialog.show()
    }

    override fun onAvailableNetwork(network: Network) {
        internetActivationDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}