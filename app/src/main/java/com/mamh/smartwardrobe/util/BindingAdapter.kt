package com.mamh.smartwardrobe.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.ui.cloth.ClothListAdapter

class BindingAdapter

//set the list in view model and bind cloth list to recycler view
@BindingAdapter("setClothList")
fun commitWifiList(listView: RecyclerView, items: List<ClothItem>?) {
    items?.let {
        (listView.adapter as ClothListAdapter).submitList(items)
    }
}




