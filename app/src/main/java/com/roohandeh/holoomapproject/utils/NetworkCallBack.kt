package com.roohandeh.holoomapproject.utils

import android.net.Network

interface NetworkCallBack {
    fun onAvailableNetwork(network: Network)
}