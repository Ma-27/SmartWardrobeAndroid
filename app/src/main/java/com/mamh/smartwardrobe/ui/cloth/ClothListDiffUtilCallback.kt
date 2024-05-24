package com.mamh.smartwardrobe.ui.cloth


/**
@description
@author Mamh
@Date 2024/5/15
 **/

import androidx.recyclerview.widget.DiffUtil
import com.mamh.smartwardrobe.bean.item.ClothItem

class ClothListDiffUtilCallback : DiffUtil.ItemCallback<ClothItem>() {
    override fun areContentsTheSame(oldItem: ClothItem, newItem: ClothItem): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: ClothItem, newItem: ClothItem): Boolean {
        return oldItem.id == newItem.id
    }
}
