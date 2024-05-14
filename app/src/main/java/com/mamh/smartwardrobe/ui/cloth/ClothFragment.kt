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


@Suppress("NAME_SHADOWING")
class ClothFragment : Fragment() {
    //本fragment的view model
    private lateinit var clothViewModel: ClothViewModel

    private var _binding: FragmentClothBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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


        setListener()

        return binding.root
    }

    //创建各个控件的点击响应监听和变量状态改变监听
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    private fun setListener() {
        // 设置SwipeRefreshLayout的下拉刷新监听
        binding.swContainer.setOnRefreshListener {
            //下拉刷新时，重新刷新衣物数据
            // 这里先不更新天气，因为数据没有更新
            binding.swContainer.isRefreshing = false
        }
    }

}