package com.roohandeh.holoomapproject.presentation.main

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.roohandeh.holoomapproject.databinding.ActivityMainBinding
import com.roohandeh.holoomapproject.presentation.base.BaseBindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

     val viewModel: MapViewModel by viewModels()


    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding = { layoutInflater ->
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }
}

