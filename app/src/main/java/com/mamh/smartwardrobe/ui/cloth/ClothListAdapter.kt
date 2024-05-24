package com.mamh.smartwardrobe.ui.cloth


/**
@description
@author Mamh
@Date 2024/5/15
 **/

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.databinding.ItemClothBinding

class ClothListAdapter(private val clickListener: ClothListClickListener) :
    ListAdapter<ClothItem, ClothListAdapter.ViewHolder>(ClothListDiffUtilCallback()) {

    // 承载每个item数据的view holder
    class ViewHolder private constructor(private val binding: ItemClothBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClothItem, clickListener: ClothListClickListener) {
            binding.clothitem = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemClothBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

class ClothListClickListener(val clickListener: (item: ClothItem) -> Unit) {
    fun onClick(item: ClothItem) = clickListener(item)
}
