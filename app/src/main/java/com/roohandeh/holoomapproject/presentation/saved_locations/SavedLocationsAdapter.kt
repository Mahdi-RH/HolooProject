package com.roohandeh.holoomapproject.presentation.saved_locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.carto.graphics.Color
import com.roohandeh.holoomapproject.databinding.SavedLocationItemViewBinding
import com.roohandeh.holoomapproject.domain.model.SavedLocation
import com.roohandeh.holoomapproject.presentation.base.BaseViewHolder

class SavedLocationsAdapter :
    ListAdapter<SavedLocation, SavedLocationsAdapter.ItemsViewHolder>(DiffConfig) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        return ItemsViewHolder(
            SavedLocationItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(android.graphics.Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(android.graphics.Color.WHITE)
        }
        holder.onBind(getItem(position))
    }

    inner class ItemsViewHolder(private val viewItem: SavedLocationItemViewBinding) :
        BaseViewHolder<SavedLocation>(viewItem.root) {
        override fun onBind(obj: SavedLocation) {
            viewItem.txtCaption.text = obj.caption
        }
    }

    object DiffConfig : DiffUtil.ItemCallback<SavedLocation>() {
        override fun areItemsTheSame(oldItem: SavedLocation, newItem: SavedLocation): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: SavedLocation, newItem: SavedLocation): Boolean {
            return oldItem == newItem
        }
    }
}