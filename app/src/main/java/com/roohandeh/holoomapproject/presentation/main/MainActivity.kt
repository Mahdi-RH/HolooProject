package com.roohandeh.holoomapproject.presentation.main

import android.view.LayoutInflater
import com.roohandeh.holoomapproject.databinding.ActivityMainBinding
import com.roohandeh.holoomapproject.presentation.base.BaseBindingActivity

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding = { layoutInflater ->
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }
}

