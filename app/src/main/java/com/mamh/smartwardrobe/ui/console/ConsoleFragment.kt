package com.mamh.smartwardrobe.ui.console

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mamh.smartwardrobe.data.serialize.CommandDatagram
import com.mamh.smartwardrobe.databinding.FragmentConsoleBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.CountDownLatch


class ConsoleFragment : Fragment() {

    private var _binding: FragmentConsoleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //本fragment的view model
    private lateinit var consoleViewModel: ConsoleViewModel

    // 初始化一个 CountDownLatch，计数设置为 1。同步任务控制
    private val latch = CountDownLatch(1)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConsoleBinding.inflate(inflater, container, false)

        consoleViewModel =
            ViewModelProvider(this, ConsoleViewModelFactory(requireActivity().application))
                .get(ConsoleViewModel::class.java)

        binding.viewmodel = consoleViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setListener()

        return binding.root
    }

    //创建各个控件的点击响应监听和变量状态改变监听
    @OptIn(DelicateCoroutinesApi::class)
    private fun setListener() {
        // 设置SwipeRefreshLayout的下拉刷新监听
        binding.swipeRefreshLayout.setOnRefreshListener {
            //下拉刷新时，重新获取传感器数据
            lifecycleScope.launch {
                Timber.d("下拉刷新触发")
                consoleViewModel.repository.setNewDataQuery(true)
                consoleViewModel.repository.transmission?.onReceiveData()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        //温度控制系统打开按钮监听
        binding.switchTemperature.setOnCheckedChangeListener { compoundButton, b ->
            // true则打开，打开后从这里处理
            if (b) {
                //打开后从这里处理
                Timber.d("温度控制系统已开启")

            } else {
                Timber.d("温度控制系统已关闭")
            }
        }

        // 灯光控制系统打开按钮监听
        binding.switchLight.setOnCheckedChangeListener { compoundButton, b ->
            // 禁用 Switch 防止用户在执行过程中改变其状态
            compoundButton.isEnabled = false

            // 根据布尔值b决定操作和备注信息
            val action = if (b) "turn_on" else "turn_off"
            val remark = if (b) "Command to turn on the light" else "Command to turn off the light"

            // 构建命令数据报
            val lightCommand = CommandDatagram.Builder()
                .setCommand("Light-Manuel")
                .setAction(action)
                .setRemark(remark)
                .build()

            // 转换为JSON字符串并发送
            val datagram = lightCommand.toJsonString()
            consoleViewModel.sendData(datagram)

            // 启用 Switch
            compoundButton.isEnabled = true
        }

        // 灯光控制系统自动控制按钮监听
        binding.switchLightAuto.setOnCheckedChangeListener { compoundButton, b ->
            // 禁用 Switch 防止用户在执行过程中改变其状态
            compoundButton.isEnabled = false

            // 确定action和remark基于布尔值b
            val action = if (b) "enable" else "disable"
            val remark = if (b)
                "Command to enable automatic light control"
            else
                "Command to disable automatic light control"

            // 构建命令数据报
            val lightControlCommand = CommandDatagram.Builder()
                .setCommand("Auto-Light-Control")
                .setAction(action)
                .setRemark(remark)
                .build()

            // 转换为JSON字符串并发送
            consoleViewModel.sendData(lightControlCommand.toJsonString())
            
            // 启用 Switch
            compoundButton.isEnabled = true
        }


        //
        binding.sbTemperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var temperature: Int = 15
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Called when the progress value is changed
                temperature = 15 + (progress * 15) / 100

                consoleViewModel.setPendingTemperature(temperature)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Called when the user starts touching the seekbar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Called when the user stops touching the seekbar

                /*
                //向主机发送温度数据,只保留温度到整数
                consoleViewModel.targetTemperature.value?.toString()
                    ?.let {
                        consoleViewModel.setTargetTemperature()
                    }
                 */
            }
        })

        // 网络状态变了，再发送新报文
        consoleViewModel.repository.userHint.observe(this, Observer {
            consoleViewModel.repository.userHint.value?.let { it ->

            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}