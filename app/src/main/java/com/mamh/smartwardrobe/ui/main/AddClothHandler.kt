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
import java.util.Random

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

    // 设置按钮点击监听器和其它控件的监听器
    private fun setListener() {
        // 点下提交键之后，创建衣物数据对象，并且更新列表
        binding.btnAddCloth.setOnClickListener {
            viewModel.addCloth(createClothItem())
            // 创建衣物数据对象后，显示成功提示信息
            viewModel.repository.setUserHint("衣物信息添加成功")
        }
    }

    /**
     * 创建并返回衣物数据对象
     */
    private fun createClothItem(): ClothItem {
        // 从 EditText 和 Spinner 中获取用户输入的衣物信息
        val clothName = etClothName.text.toString()
        val clothStyle = etClothStyle.text.toString()
        val clothMaterial = etMaterial.text.toString()
        val clothSize = etClothSize.text.toString()
        // 生成唯一标识符
        val uniqueId = generateUniqueId()

        // 获取 Spinner 中选中的颜色
        val selectedColor = spinnerColors.selectedItem?.toString() ?: ""

        // 在此创建衣物数据对象，将用户输入的信息赋给 ClothItem 的属性
        return ClothItem(
            id = uniqueId,
            name = clothName,
            colors = listOf(selectedColor),
            style = clothStyle,
            material = clothMaterial,
            size = clothSize,
            isInCloset = false,
            hangPosition = 0,
            brand = "brand_default",
            purchaseDate = "2024-5-27",
            isClean = true,
            lastWornDate = "2024-4-11",
            tags = mutableListOf("tag1", "tag2"),
            recommendationScore = 0.0
        )
    }

    private fun generateUniqueId(): String {
        // 获取当前时间戳
        val timestamp = System.currentTimeMillis()

        // 生成随机数，用于加入标识符中增加一定的随机性
        val random = Random().nextInt(1000)

        // 将时间戳和随机数拼接成字符串作为唯一标识符
        return "${timestamp}_${random}"
    }

}



