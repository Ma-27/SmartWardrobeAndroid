package com.mamh.smartwardrobe.ui.cloth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var listAdapter: ClothListAdapter

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
            listAdapter = ClothListAdapter(ClothListClickListener { item ->

                Timber.d("成功点击$item")
            })

            binding.rvClothList.adapter = listAdapter
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
            listAdapter.notifyDataSetChanged()

            // 下拉刷新时，重新刷新衣物数据
            binding.swContainer.isRefreshing = false

            binding.viewmodel!!.repository.setUserHint("衣物数据已刷新")
        }
    }

}