package com.mamh.smartwardrobe.util

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mamh.smartwardrobe.R
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


// set custom text for cloth item
@BindingAdapter("customDescriptionText")
fun setCustomText(textView: TextView, clothItem: ClothItem?) {
    clothItem?.let {
        val formattedText = "${it.style} | ${it.material} | ${it.tags.joinToString(" | ")}"
        textView.text = formattedText
    }
}


// set the color of the color block
@BindingAdapter("clothColor", "colorIndex")
fun setClothColor(view: View, colors: List<String>?, index: Int) {
    if (!colors.isNullOrEmpty() && index >= 0 && index < colors.size) {
        view.setBackgroundColor(Color.parseColor(colors[index]))
    } else {
        // 设置默认颜色或处理错误
        view.setBackgroundColor(Color.parseColor("#FFFFFF")) // 默认白色
    }
}

@BindingAdapter("categoryImage")
fun setCategoryImage(view: ImageView, category: String?) {
    when (category) {
        "上衣" -> view.setImageResource(R.drawable.cloth1)
        "裤子" -> view.setImageResource(R.drawable.cloth2)
        "鞋袜" -> view.setImageResource(R.drawable.cloth3)
        "帽子" -> view.setImageResource(R.drawable.cloth4)
        else -> view.setImageResource(R.drawable.cloth1) // 默认图片
    }
}




