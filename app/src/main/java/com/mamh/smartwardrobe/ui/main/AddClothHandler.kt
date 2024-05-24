package com.mamh.smartwardrobe.ui.main


/**
@description
@author Mamh
@Date 2024/5/23
 **/


import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.databinding.DialogAddClothBinding

class AddClothHandler(
    private val binding: DialogAddClothBinding,
    private val viewModel: MainActivityViewModel
) {

    private val btnAddCloth: Button = binding.btnAddCloth
    private val etClothName: EditText = binding.etAddClothName
    private val spinnerColors: Spinner = binding.spinnerColors
    private val etClothStyle: EditText = binding.etClothStyle
    private val etMaterial: EditText = binding.etMaterial
    private val etClothSize: EditText = binding.etTextSize

    init {
        // 控件的点击逻辑
        setListener()
    }


    // 各个部件的点击监听
    private fun setListener() {
        // 点下提交键之后，创建衣物数据对象，并且更新列表
        binding.btnAddCloth.setOnClickListener {
            viewModel.addCloth(createClothItem())
            // 创建衣物数据对象
            viewModel.repository.setUserHint("衣物信息添加成功")
        }
    }

    /**
     * 创建并返回衣物数据对象
     */
    private fun createClothItem(): ClothItem {
        val clothName = etClothName.text.toString()
        val clothColor = spinnerColors.selectedItem.toString()
        val clothStyle = etClothStyle.text.toString()
        val clothMaterial = etMaterial.text.toString()
        val clothSize = etClothSize.text.toString()

        // 在此创建衣物数据对象
        return ClothItem(
            id = "", // 你可以根据需求生成一个唯一标识符
            name = clothName,
            colors = listOf(clothColor),
            style = clothStyle,
            material = clothMaterial,
            size = clothSize,
            isInCloset = false,
            hangPosition = 0,
            brand = "",
            purchaseDate = "",
            isClean = true,
            lastWornDate = "",
            tags = mutableListOf(),
            recommendationScore = 0.0
        )
    }
}


