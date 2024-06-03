package com.mamh.smartwardrobe.ui.cloth

/**
@description
author Mamh
@Date 2024/5/15
 **/

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.databinding.ItemClothBinding

class ClothListAdapter(val clickListener: ClothListClickListener) :
    ListAdapter<ClothItem, ClothListAdapter.ViewHolder>(ClothListDiffUtilCallback()) {

    // 承载每个item数据的view holder
    class ViewHolder private constructor(private val binding: ItemClothBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClothItem, clickListener: ClothListClickListener) {
            binding.clothitem = item
            binding.executePendingBindings()
            binding.clickListener = clickListener

            // 设置点击监听器
            itemView.setOnClickListener {
                clickListener.onClick(item)
            }

            // 设置长按监听器
            itemView.setOnLongClickListener {
                clickListener.onLongClick(item)
                true
            }

            binding.btnDelete.setOnClickListener {
                clickListener.onDeleteClick(item)
            }
        }

        companion object {
            // 从父级创建view holder
            fun from(parent: ViewGroup): ViewHolder {
                // 从布局中创建binding
                val layoutInflater = LayoutInflater.from(parent.context)
                // 保存binding
                val binding = ItemClothBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    // 创建view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // 视图和数据绑定
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

// 点击监听器
class ClothListClickListener
    (
    // 点击监听器
    val clickListener: (item: ClothItem) -> Unit,
    // 长按监听器
    val longClickListener: (item: ClothItem) -> Unit,
    // 左滑监听器（将衣物从衣柜中拿出）
    val swipeLeftListener: (item: ClothItem) -> Unit,
    // 右滑监听器（将衣物自动归类到衣柜）
    val swipeRightListener: (item: ClothItem) -> Unit,
    // 删除按钮监听器
    val deleteClickListener: (item: ClothItem) -> Unit
) {
    fun onClick(item: ClothItem) = clickListener(item)
    fun onLongClick(item: ClothItem) = longClickListener(item)
    fun onSwipeLeft(item: ClothItem) = swipeLeftListener(item)
    fun onSwipeRight(item: ClothItem) = swipeRightListener(item)
    fun onDeleteClick(item: ClothItem) = deleteClickListener(item)
}
