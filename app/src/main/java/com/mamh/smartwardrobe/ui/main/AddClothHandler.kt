package com.mamh.smartwardrobe.ui.main


/**
@description
@author Mamh
@Date 2024/5/23
 **/


import android.content.Context
import android.graphics.Color
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.data.AppRepository
import com.mamh.smartwardrobe.databinding.DialogAddClothBinding
import com.mamh.smartwardrobe.ui.cloth.ClothModel
import com.mamh.smartwardrobe.util.Utility
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog
import timber.log.Timber
import java.util.Random


class AddClothHandler(
    private val binding: DialogAddClothBinding,
    private val viewModel: MainActivityViewModel,
    private val context: Context
) {

    private val btnAddCloth: Button = binding.btnAddCloth
    private val etClothName: EditText = binding.etAddClothName

    private val etClothStyle: EditText = binding.etClothStyle
    private val etMaterial: EditText = binding.etMaterial
    private val etClothLabel: EditText = binding.etLabels

    // 用于显示颜色的视图
    private val viewSelectedColor1 = binding.viewSelectedColor1
    private val viewSelectedColor2 = binding.viewSelectedColor2
    private val viewSelectedColor3 = binding.viewSelectedColor3

    // 选择的衣物类别
    private val spinnerCategory: Spinner = binding.spinnerClothCategory


    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()
    var repository: AppRepository = _repository


    // 用于存储颜色信息的集合
    private val selectedColors: MutableSet<String> = HashSet()

    init {
        // 控件的点击逻辑
        setListener()
    }

    // 设置按钮点击监听器和其它控件的监听器
    private fun setListener() {
        // 点下提交键之后，创建衣物数据对象，并且更新列表
        binding.btnAddCloth.setOnClickListener {
            createClothItem()?.let { it1 -> viewModel.addCloth(it1) }
            // 创建衣物数据对象后，显示成功提示信息
            viewModel.repository.setUserHint("衣物信息添加成功")
        }

        binding.btnColorPicker.setOnClickListener {
            // 跳转到颜色选择界面
            // 选择颜色后，将颜色信息填入到方块视图中

            // 显示对话框的代码
            val dialog = ColorChooserDialog(context)
            dialog.setTitle("选择颜色")
            dialog.setColorListener { v, color ->
                // 如果已经有3种颜色了，则不再设置
                if (!selectedColors.contains(color)) {
                    // 输出颜色集合中的颜色
                    Timber.d("Color selected:$color")
                    // 将选择的颜色添加到已选择颜色集合中
                    selectedColors.add(color)
                    // 将选择的颜色设置到视图上
                    setColors(color)
                }
                Timber.d(selectedColors.size.toString())
            }
            dialog.show()
        }


        // 第一个色块的监听
        binding.viewSelectedColor1.setOnClickListener {
            // 弹出提示框，重新选颜色
            val dialog = ColorChooserDialog(context)
            dialog.setTitle("选择颜色")
            dialog.setColorListener { v, color ->
                // 将选择的颜色设置到视图上
                setColors(color, 1)
                Timber.d("Color block 1 set to:$color")
            }
            dialog.show()
        }

        // 第二个色块的监听
        binding.viewSelectedColor2.setOnClickListener {
            // 弹出提示框，重新选颜色
            val dialog = ColorChooserDialog(context)
            dialog.setTitle("选择颜色")
            dialog.setColorListener { v, color ->
                // 将选择的颜色设置到视图上
                setColors(color, 2)
                Timber.d("Color block 2 set to:$color")
            }
            dialog.show()
        }

        // 第三个色块的监听
        binding.viewSelectedColor3.setOnClickListener {
            // 弹出提示框，重新选颜色
            val dialog = ColorChooserDialog(context)
            dialog.setTitle("选择颜色")
            dialog.setColorListener { v, color ->
                // 将选择的颜色设置到视图上
                setColors(color, 3)
                Timber.d("Color block 3 set to:$color")
            }
            dialog.show()
        }
    }

    /**
     * 设置颜色视图的背景颜色
     */
    private fun setColors(hexColor: String) {
        // 根据已选择的颜色数量设置到对应的视图上
        when (selectedColors.size) {
            1 -> viewSelectedColor1.setBackgroundColor(Color.parseColor(hexColor))
            2 -> viewSelectedColor2.setBackgroundColor(Color.parseColor(hexColor))
            3 -> viewSelectedColor3.setBackgroundColor(Color.parseColor(hexColor))
        }
    }

    /**
     * 设置颜色视图的背景颜色,但是给指定的view设置
     */
    private fun setColors(hexColor: String, who: Int) {
        // 根据已选择的颜色数量设置到对应的视图上
        when (who) {
            1 -> viewSelectedColor1.setBackgroundColor(Color.parseColor(hexColor))
            2 -> viewSelectedColor2.setBackgroundColor(Color.parseColor(hexColor))
            3 -> viewSelectedColor3.setBackgroundColor(Color.parseColor(hexColor))
        }
    }


    /**
     * 创建并返回衣物数据对象
     */
    private fun createClothItem(): ClothItem? {
        // 从 EditText 和 Spinner 中获取用户输入的衣物信息

        // 名称
        val clothName = etClothName.text.toString()
        // 款式
        val clothStyle = etClothStyle.text.toString()
        // 材质
        val clothMaterial = etMaterial.text.toString()
        // 标签
        val clothLabel =
            etClothLabel.text.toString().takeIf { it.isNotBlank() } ?: "青春活力 | 清爽舒适"
        // 获取 Spinner 中选中的衣物类别
        val selectedCategory = spinnerCategory.selectedItem?.toString() ?: "衬衫"

        // 检查名称、款式和材质是否为空串，若为空串则返回空值
        if (clothName.isBlank() || clothStyle.isBlank() || clothMaterial.isBlank()) {
            // 显示错误提示信息
            viewModel.repository.setUserHint("名称、款式和材质不能是空的哦")
            return null
        }

        // 生成唯一标识符
        val uniqueId = generateUniqueId()

        // 推荐系统生成的标签
        val generatedFeature: String = "最近常穿"

        // 在此创建衣物数据对象，将用户输入的信息赋给 ClothItem 的属性
        val clothItem = ClothItem(
            id = uniqueId,
            name = clothName,
            category = selectedCategory,
            colors = selectedColors.toList(), // 将集合转换为列表并赋值给 colors 属性
            style = clothStyle,
            material = clothMaterial,
            size = "default size",
            isInCloset = true,
            hangPosition = 0,
            brand = "brand_default",
            purchaseDate = "2024-5-15",
            isClean = true,
            lastWornDate = Utility.getCurrentDate(),
            tags = mutableListOf(clothLabel, generatedFeature),
            recommendationScore = 0.0
        )

        // 计算推荐评分
        clothItem.recommendationScore = ClothModel.recommend(clothItem)

        return clothItem
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



