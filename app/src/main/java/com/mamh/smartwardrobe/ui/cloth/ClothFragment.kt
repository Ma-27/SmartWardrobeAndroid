package com.mamh.smartwardrobe.ui.cloth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mamh.smartwardrobe.databinding.FragmentClothBinding
import kotlinx.coroutines.DelicateCoroutinesApi
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
                    viewModel.repository.setUserHint("衣物自动归位中...")
                    // 在这里添加处理左滑的逻辑，例如发送控制命令到智能衣柜硬件系统
                },
                swipeRightListener = { item ->
                    Timber.d("右滑$item")
                    viewModel.repository.setUserHint("拾取这件衣服中...")
                    // 在这里添加处理右滑的逻辑，例如发送控制命令到智能衣柜硬件系统
                },
                deleteClickListener = { item ->
                    Timber.d("删除$item")
                    viewModel.repository.setUserHint("正在删除此衣物...")
                    // 在这里添加删除逻辑，例如更新数据库或数据源
                    viewModel.removeClothItem(item)
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

            // 通知适配器数据已更新
            adapter.notifyDataSetChanged()

            // 下拉刷新时，重新刷新衣物数据
            binding.swContainer.isRefreshing = false

            binding.viewmodel!!.repository.setUserHint("衣物数据已刷新")
        }
    }

}