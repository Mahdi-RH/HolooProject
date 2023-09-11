package com.roohandeh.holoomapproject.presentation.saved_locations

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roohandeh.holoomapproject.R
import com.roohandeh.holoomapproject.databinding.FragmentSavedLocationsBinding
import com.roohandeh.holoomapproject.presentation.base.BaseBindingFragment
import com.roohandeh.holoomapproject.presentation.main.MainActivity
import com.roohandeh.holoomapproject.presentation.main.MapViewModel
import com.roohandeh.holoomapproject.utils.showToast


class SavedLocationsFragment : BaseBindingFragment<FragmentSavedLocationsBinding>() {

    private lateinit var viewModel: MapViewModel
    private lateinit var mainActivity: MainActivity

    private val adapter: SavedLocationsAdapter by lazy {
        SavedLocationsAdapter()
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSavedLocationsBinding =
        { layoutInflater, viewGroup, attachedToParent ->
            FragmentSavedLocationsBinding.inflate(layoutInflater, viewGroup, attachedToParent)
        }

    override fun initView() {
        mainActivity = activity as MainActivity
        viewModel = mainActivity.viewModel
        viewModel.getLocations()
        observeSavedLocations()
        binding.recyclerSavedLocations.adapter = adapter
    }


    private fun observeSavedLocations() {
        viewModel.savedLocations.observe(viewLifecycleOwner) { savedLocationState ->
            when {
                savedLocationState.loading -> {
                    mainActivity.setProgressbarVisibility(true)
                }

                savedLocationState.savedLocations != null -> {
                    mainActivity.setProgressbarVisibility(false)
                    if (savedLocationState.savedLocations.isEmpty().not()) {
                        adapter.submitList(savedLocationState.savedLocations)
                        mainActivity.hideTopViewMessage()
                    } else {
                        context?.let {
                            mainActivity.showTopViewMessage(it.resources.getString(R.string.no_saved_location_message))
                        }
                    }
                }

                savedLocationState.errorMessage.isNullOrEmpty().not() -> {
                    mainActivity.setProgressbarVisibility(false)
                    showToast(savedLocationState.errorMessage!!)
                }
            }
        }
    }

}