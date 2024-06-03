package com.mamh.smartwardrobe.ui.cloth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mamh.smartwardrobe.R
import com.mamh.smartwardrobe.data.database.SmartWardrobeDatabase
import com.mamh.smartwardrobe.data.database.cloth.ClothDao
import com.mamh.smartwardrobe.data.serialize.CommandDatagram
import com.mamh.smartwardrobe.databinding.FragmentClothBinding
import com.mamh.smartwardrobe.util.DataTransferObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("NAME_SHADOWING")
class ClothFragment : Fragment() {
    //本fragment的view model
    private lateinit var clothViewModel: ClothViewModel

    private var _binding: FragmentClothBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: ClothListAdapter

    // 获取数据库中衣物表实例
    private lateinit var clothDao: ClothDao

    // 衬衫筛选开关
    private var isShirtFilterEnabled = false

    // 裤子筛选开关
    private var isTrousersFilterEnabled = false

    // 鞋袜筛选开关
    private var isShoesFilterEnabled = false

    // 帽子筛选开关
    private var isHatFilterEnabled = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClothBinding.inflate(inflater, container, false)

        clothViewModel =
            ViewModelProvider(this, ClothViewModelFactory(requireActivity().application))
                .get(ClothViewModel::class.java)

        binding.viewmodel = clothViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        clothDao = SmartWardrobeDatabase.getInstance(requireActivity().application).clothDao


        setupAdapter()

        setListener()

        return binding.root
    }

    //创建recycler view的适配器和recycler view的点击响应
    private fun setupAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null) {
            // 初始化recycler view adapter也在这里设置点击监听，点击即弹出对话框配置网络参数
            adapter = ClothListAdapter(ClothListClickListener(
                clickListener = { item ->
                    Timber.d("成功点击$item")
                },


                longClickListener = { item ->
                    Timber.d("成功长按$item")
                    viewModel.repository.setUserHint("还有哪件想要一起搭配呢？ (◍´ಲ`◍)")
                },


                swipeLeftListener = { item ->
                    Timber.d("左滑$item")
                    // 在这里添加处理左滑的逻辑，例如发送控制命令到智能衣柜硬件系统

                    // 转化item到MCU的衣物数据结构
                    val mcuItem = DataTransferObject.fromClothItemToMCU(item)

                    // 包装报文
                    val homingCommand = CommandDatagram.Builder()
                        .setDeviceId(1)
                        .setFrom(0)
                        .setPacketType("Command")
                        .setCommand("Actuator-Control")
                        .setActuator("Shelf")
                        .setAction("homing")
                        .setRemark("Command aims to homing cloth to an empty position.")
                        .setCloth(mcuItem) // 添加 setCloth 调用
                        .build()

                    // 转换为JSON字符串并发送
                    val datagram = homingCommand.toJsonString()
                    clothViewModel.sendData(datagram)

                    Timber.d(datagram)
                    viewModel.repository.setUserHint("衣物自动归位中...")
                },


                swipeRightListener = { item ->
                    Timber.d("右滑$item")
                    // 在这里添加处理右滑的逻辑，例如发送控制命令到智能衣柜硬件系统

                    // 转化item到MCU的衣物数据结构
                    val mcuItem = DataTransferObject.fromClothItemToMCU(item)

                    // 包装报文
                    val fetchCommand = CommandDatagram.Builder()
                        .setDeviceId(1)
                        .setFrom(0)
                        .setPacketType("Command")
                        .setCommand("Actuator-Control")
                        .setActuator("Shelf")
                        .setAction("fetch")
                        .setRemark("Command to fetch the cloth to the default position.")
                        .setCloth(mcuItem) // 添加 setCloth 调用
                        .build()

                    // 转换为JSON字符串并发送
                    val datagram = fetchCommand.toJsonString()
                    clothViewModel.sendData(datagram)

                    Timber.d(datagram)
                    viewModel.repository.setUserHint("拾取这件衣服中...")
                },


                deleteClickListener = { item ->
                    Timber.d("删除$item")
                    viewModel.repository.setUserHint("正在删除此衣物...")
                    // 在这里添加删除逻辑，例如更新数据库或数据源
                    lifecycleScope.launch {
                        viewModel.removeClothItem(item, clothDao)
                    }
                }
            ))

            binding.rvClothList.adapter = adapter

            // 配置 ItemTouchHelper
            val itemTouchHelper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = adapter.currentList[position]
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            // 处理左滑事件
                            adapter.notifyItemChanged(position)
                            adapter.clickListener.onSwipeLeft(item)
                        }

                        ItemTouchHelper.RIGHT -> {
                            // 处理右滑事件
                            adapter.notifyItemChanged(position)
                            adapter.clickListener.onSwipeRight(item)
                        }
                    }
                }
            })

            itemTouchHelper.attachToRecyclerView(binding.rvClothList)

        } else {
            Timber.e("试图设置adapter时ViewModel尚未初始化。")
        }
    }

    //创建各个控件的点击响应监听和变量状态改变监听
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    private fun setListener() {
        // 设置SwipeRefreshLayout的下拉刷新监听
        binding.swContainer.setOnRefreshListener {
            // 请求数据库数据


            // 通知MCU，同步数据列表 todo


            // 通知适配器数据已更新
            adapter.notifyDataSetChanged()

            // 下拉刷新时，重新刷新衣物数据
            binding.swContainer.isRefreshing = false

            binding.viewmodel!!.repository.setUserHint("衣物数据已刷新")
        }


        // 监听衣物列表数据变化，如果数据变化，更新adapter。如果列表为空，则请求数据库数据
        clothViewModel.clothList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                // 请求数据库数据
                GlobalScope.launch {
                    clothDao.getAllClothes()
                }
            }
            adapter.submitList(it)
        }


        // 监听数据库的变化
        clothDao.getAllClothes().observe(viewLifecycleOwner) { clothEntities ->
            Timber.d("获取数据库中的衣物列表到页面中")

            // 将 ClothItemEntity 列表转换为 ClothItem 列表
            val clothItems =
                DataTransferObject.fromClothItemEntityListToClothItemList(clothEntities)

            // 更新 ViewModel 中的 LiveData
            clothViewModel.setClothList(clothItems)

            // 数据库数据变化时，更新adapter
            adapter.submitList(clothItems)
        }


        // 实现分类功能，通过标签筛选是衬衫的衣物
        binding.btnShirt.setOnClickListener {
            isShirtFilterEnabled = setupFilterButton(
                binding.btnShirt,
                isShirtFilterEnabled,
                "衬衫",
                R.color.green_heavy,
                R.color.white
            )
        }


        // 实现分类功能，通过标签筛选是裤子的衣物
        binding.btnTrousers.setOnClickListener {
            isTrousersFilterEnabled = setupFilterButton(
                binding.btnTrousers,
                isTrousersFilterEnabled,
                "裤子",
                R.color.green_heavy,
                R.color.white
            )
        }


        // 实现分类功能，通过标签筛选是鞋袜的衣物
        binding.btnShoes.setOnClickListener {
            isShoesFilterEnabled = setupFilterButton(
                binding.btnShoes,
                isShoesFilterEnabled,
                "鞋袜",
                R.color.green_heavy,
                R.color.white
            )
        }


        // 实现分类功能，通过标签筛选是帽子的衣物
        binding.btnHat.setOnClickListener {
            isHatFilterEnabled = setupFilterButton(
                binding.btnHat,
                isHatFilterEnabled,
                "帽子",
                R.color.green_heavy,
                R.color.white
            )
        }

    }


    // 定义一个通用的函数来处理筛选逻辑
    private fun setupFilterButton(
        button: Button,
        filterEnabled: Boolean,
        category: String,
        colorSelected: Int,
        colorDefault: Int
    ): Boolean {
        val newFilterEnabled = !filterEnabled
        button.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (newFilterEnabled) colorSelected else colorDefault
        )

        clothViewModel.clothList.observe(viewLifecycleOwner) {
            val filteredList = if (newFilterEnabled) {
                it.filter { item -> item.category == category }
            } else {
                it
            }
            adapter.submitList(filteredList)
        }

        return newFilterEnabled
    }

}