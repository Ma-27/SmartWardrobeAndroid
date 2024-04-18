package com.mamh.smartwardrobe.ui.datacenter

import androidx.recyclerview.widget.DiffUtil
import com.mamh.smartwardrobe.bean.item.DataItem

class DataCenterDiffUtilCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}