package com.mamh.smartwardrobe.ui.datacenter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mamh.smartwardrobe.databinding.FragmentDatacenterBinding
import kotlinx.coroutines.DelicateCoroutinesApi

class DataCenterFragment : Fragment() {
    //本fragment的view model
    private lateinit var dataCenterViewModel: DataCenterViewModel

    private var _binding: FragmentDatacenterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatacenterBinding.inflate(inflater, container, false)

        dataCenterViewModel =
            ViewModelProvider(this, DataCenterViewModelFactory(requireActivity().application))
                .get(DataCenterViewModel::class.java)

        binding.viewmodel = dataCenterViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setListener()

        return binding.root
    }


    //创建各个控件的点击响应监听和变量状态改变监听
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    private fun setListener() {
        // 设置SwipeRefreshLayout的下拉刷新监听
        binding.swContainer1.setOnRefreshListener {
            // 下拉刷新时，重新刷新衣物数据
            binding.swContainer1.isRefreshing = false
        }
    }


}