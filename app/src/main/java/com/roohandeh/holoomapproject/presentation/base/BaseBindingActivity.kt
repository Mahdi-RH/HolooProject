package com.roohandeh.holoomapproject.presentation.base

import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.roohandeh.holoomapproject.databinding.ActivityBaseBinding
import com.roohandeh.holoomapproject.utils.AlertCreator
import com.roohandeh.holoomapproject.utils.ConnectivityObserver
import com.roohandeh.holoomapproject.utils.GpsReceiver
import com.roohandeh.holoomapproject.utils.GpsStatus
import com.roohandeh.holoomapproject.utils.NetworkConnectivityManager
import com.roohandeh.holoomapproject.utils.isGpsEnabled
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 *  A parent for every activity that want to implement viewBinding.
 *
 */
abstract class
BaseBindingActivity<out VB : ViewBinding> : AppCompatActivity() {

    private lateinit var _binding: ViewBinding
    abstract val bindingInflater: (LayoutInflater) -> VB
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB
    private lateinit var baseViewBinding: ActivityBaseBinding
    private lateinit var networkConnectivityManager: NetworkConnectivityManager
    private val gpsSwitchStateReceiver = GpsReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkConnectivityManager = NetworkConnectivityManager(this)
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

    fun checkGpsAvailability() {
        if (isGpsEnabled(this)) {
            AlertCreator.dismissGpsActivationDialog()
        } else {
            AlertCreator.showGpsActivationDialog(this)
        }
        gpsSwitchStateReceiver.gpsStatus.observe(this) {
            if (it == GpsStatus.Available) {
                AlertCreator.dismissGpsActivationDialog()
            } else if (it == GpsStatus.UnAvailable) {
                AlertCreator.showGpsActivationDialog(this)
            }
        }
    }

    fun checkInternetAvailability() {
        if (networkConnectivityManager.isNetworkAvailable()) {
            AlertCreator.dismissInternetActivationDialog()
        } else {
            AlertCreator.showInternetActivationDialog(this)
        }
        networkConnectivityManager.observe().onEach {
            when (it) {
                ConnectivityObserver.Status.Available -> {
                    runOnUiThread {
                        AlertCreator.dismissInternetActivationDialog()
                    }
                }
                ConnectivityObserver.Status.UnAvailable -> {
                    runOnUiThread {
                        AlertCreator.showInternetActivationDialog(this)
                    }
                }
                ConnectivityObserver.Status.Lost -> {
                    runOnUiThread {
                        AlertCreator.showInternetActivationDialog(this)
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        registerGpsSwitchStateReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterGpsSwitchStateReceiver()
    }

    private fun registerGpsSwitchStateReceiver() {
        registerReceiver(
            gpsSwitchStateReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }

    private fun unRegisterGpsSwitchStateReceiver() {
        unregisterReceiver(gpsSwitchStateReceiver)
    }
}