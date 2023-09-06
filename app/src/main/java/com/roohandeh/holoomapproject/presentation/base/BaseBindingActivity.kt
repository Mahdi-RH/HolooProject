package com.roohandeh.holoomapproject.presentation.base

import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.roohandeh.holoomapproject.databinding.ActivityBaseBinding
import com.roohandeh.holoomapproject.utils.GpsReceiver
import com.roohandeh.holoomapproject.utils.NetworkConnectivityManager

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
    lateinit var networkConnectivityManager : NetworkConnectivityManager
      val gpsSwitchStateReceiver = GpsReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseViewBinding = ActivityBaseBinding.inflate(layoutInflater)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(_binding.root)
        networkConnectivityManager = NetworkConnectivityManager(this)
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

    fun showTopViewMessage(message: String) {
        baseViewBinding.textTopViewMessage.visibility = View.VISIBLE
        baseViewBinding.textTopViewMessage.text = message
    }

    fun hideTopViewMessage() {
        baseViewBinding.textTopViewMessage.visibility = View.GONE
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