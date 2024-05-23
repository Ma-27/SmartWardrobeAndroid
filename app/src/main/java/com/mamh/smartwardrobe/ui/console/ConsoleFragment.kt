package com.mamh.smartwardrobe.ui.console

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
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
    // 定义请求位置权限的请求代码
    private val LOCATION_REQUEST_CODE = 106


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
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    private fun setListener() {
        // 设置SwipeRefreshLayout的下拉刷新监听
        binding.swipeRefreshLayout.setOnRefreshListener {
            //下拉刷新时，重新获取传感器数据
            lifecycleScope.launch {
                Timber.d("下拉刷新触发")
                consoleViewModel.repository.setNewDataQuery(true)
                consoleViewModel.repository.transmission?.onReceiveData()

                val connMgr: ConnectivityManager? =
                    requireActivity().application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                var networkInfo: NetworkInfo? = null

                if (connMgr != null) {
                    networkInfo = connMgr.activeNetworkInfo
                }

                if (networkInfo != null && networkInfo.isConnected) {
                    // 请求新的经纬度
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // 请求权限
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            LOCATION_REQUEST_CODE
                        )
                    } else {
                        val locationManager =
                            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0f,
                            object :
                                LocationListener {
                                override fun onLocationChanged(location: Location) {
                                    // 获取到位置信息后，在这里执行获取新经纬度的操作
                                    val formattedLatLng =
                                        String.format(
                                            "%.4f,%.4f",
                                            location.longitude,
                                            location.latitude
                                        )

                                    Timber.d(formattedLatLng.toString())

                                    // 更新viewmodel中latlng的新值
                                    consoleViewModel.repository.setLatLng(formattedLatLng)
                                    // 移除位置更新，避免重复获取位置数据
                                    locationManager.removeUpdates(this)
                                }

                                override fun onStatusChanged(
                                    provider: String?,
                                    status: Int,
                                    extras: Bundle?
                                ) {
                                }

                                override fun onProviderEnabled(provider: String) {}
                                override fun onProviderDisabled(provider: String) {}
                            })
                    }
                }
                // 这里先不更新天气，因为数据没有更新
                binding.swipeRefreshLayout.isRefreshing = false
                binding.viewmodel!!.repository.setUserHint("智能衣柜数据已刷新")
            }
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


        // 处理视图派遣冲突
        binding.sbTemperature.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 当用户开始触摸 `SeekBar` 时，请求不拦截触摸事件
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP -> {
                    // 当用户停止触摸 `SeekBar` 时，允许拦截触摸事件
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            // false 让 `SeekBar` 的原生触摸功能正常处理
            false
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

        // 观察经纬度信息，如果经纬度变了，才刷新
        consoleViewModel.repository.latlng.observe(viewLifecycleOwner, object : Observer<String> {
            private var lastObservedLatLng = ""
            override fun onChanged(newLatLng: String) {
                // 检查新的经纬度是否与上一次观察到的不同
                if (newLatLng != lastObservedLatLng) {
                    // 更新最后观察到的经纬度
                    lastObservedLatLng = newLatLng

                    // 经纬度有变化，从云端API更新天气数据


                    lifecycleScope.launch {
                        /*
                        consoleViewModel.repository.updateWeatherFromRemote()?.let {
                            // 通过函数更新每个LiveData的值

                            consoleViewModel.setHumidity(it.humidity)
                            consoleViewModel.setPmIndex(it.pm25)
                            consoleViewModel.setTemperature(it.temperature)  // 假设温度后有°C，去除它并转换为整数
                            consoleViewModel.setLocation(it.location)
                            consoleViewModel.setWeatherCondition(it.weatherCondition)
                            consoleViewModel.setClothingSuggestion(it.dressingAdvice)


                        }

                         */
                    }


                    // fixme
                }
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}