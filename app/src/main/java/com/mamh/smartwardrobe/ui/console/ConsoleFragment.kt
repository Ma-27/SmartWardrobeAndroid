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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mamh.smartwardrobe.bean.netpacket.UsefulDailyWeatherDetail
import com.mamh.smartwardrobe.data.database.SmartWardrobeDatabase
import com.mamh.smartwardrobe.data.database.weather.WeatherDao
import com.mamh.smartwardrobe.data.serialize.CommandDatagram
import com.mamh.smartwardrobe.databinding.FragmentConsoleBinding
import com.mamh.smartwardrobe.util.DataTransferObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    // 获取数据库中天气表实例
    private lateinit var weatherDao: WeatherDao

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

        weatherDao = SmartWardrobeDatabase.getInstance(requireActivity().application).weatherDao

        setListener()

        return binding.root
    }

    //创建各个控件的点击响应监听和变量状态改变监听
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    private fun setListener() {

        /// ---------------------------------根视图与数据处理------------------------------------------------------

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

                        // 这里更新天气数据
                        binding.viewmodel!!.repository.setUserHint("智能衣柜数据已刷新")
                    }
                }
                // 如果获取不到网络信息，则使用数据库中的更新
                else {
                    // 网络连接失败时，从Room数据库中加载天气信息
                    queryWeatherRoomDatabase()
                }
                // 停止刷新动画
                binding.swipeRefreshLayout.isRefreshing = false
            }
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
                        // 从远程API更新天气数据
                        val weather = consoleViewModel.repository.updateWeatherFromRemote()
                            ?.let { weatherDetail ->
                                Timber.d("Weather data updated from remote API: $weatherDetail")
                                // 将其存储
                                consoleViewModel.setWeatherDetail(weatherDetail)
                                // 记录更新结果
                                Timber.d("Weather data by transformation to live data: $weatherDetail")

                                // 将其更新到数据库中
                                updateWeatherRoomDatabase(weatherDetail)

                                weatherDetail // 返回非空的 UsefulDailyWeatherDetail 对象
                            }
                        // 更新结束
                    }
                }
            }
        })


        /// ---------------------------- 监控温度部分 --------------------------------------------------------

        // 自动温度控制打开按钮监听
        binding.switchTemperature.setOnCheckedChangeListener { compoundButton, b ->
            // 禁用 Switch 防止用户在执行过程中改变其状态
            compoundButton.isEnabled = false

            // 根据布尔值b决定操作和备注信息
            val action = if (b) "enable" else "disable"
            val remark =
                if (b) "Command to enable the auto control target temperature" else "Command to disable the auto control target temperature"

            // 构建命令数据报
            val temperatureCommand = CommandDatagram.Builder()
                .setCommand("Temperature-Control")
                .setAction(action)
                .setRemark(remark)
                .setTarget(consoleViewModel.targetTemperature.value!!)
                .build()

            // 转换为JSON字符串并发送
            val datagram = temperatureCommand.toJsonString()
            consoleViewModel.sendData(datagram)

            // 启用 Switch
            compoundButton.isEnabled = true

            Timber.d("Datagram send to auto control temperature:$temperatureCommand")
        }


        // 增温控制
        binding.segmentedButtonTemperatureHeat.addOnCheckedChangeListener { materialButton, b ->
            // 禁用 按钮 防止用户在执行过程中改变其状态
            materialButton.isEnabled = false

            // 根据布尔值b决定操作和备注信息
            val action = if (b) "turn_on" else "turn_off"
            val remark =
                if (b) "Command to turn on the heater." else "Command to turn off the heater."

            // 构建命令数据报
            val heaterCommand = CommandDatagram.Builder()
                .setCommand("Actuator-Control")
                .setAction(action)
                .setRemark(remark)
                .setActuator("Heater")
                .build()

            // 转换为JSON字符串并发送
            val datagram = heaterCommand.toJsonString()
            consoleViewModel.sendData(datagram)

            // 重新启用 按钮
            materialButton.isEnabled = true

            Timber.d("Datagram send to turn on/off heater:$heaterCommand")
        }


        // 降温控制
        binding.segmentedButtonTemperatureCool.addOnCheckedChangeListener { materialButton, b ->
            // 禁用 按钮 防止用户在执行过程中改变其状态
            materialButton.isEnabled = false

            // 根据布尔值b决定操作和备注信息
            val action = if (b) "turn_on" else "turn_off"
            val remark =
                if (b) "Command to turn on the cooler." else "Command to turn off the cooler."

            // 构建命令数据报
            val coolerCommand = CommandDatagram.Builder()
                .setCommand("Actuator-Control")
                .setAction(action)
                .setRemark(remark)
                .setActuator("Cooler")
                .setTarget(255)
                .build()

            // 转换为JSON字符串并发送
            val datagram = coolerCommand.toJsonString()
            consoleViewModel.sendData(datagram)

            // 重新启用 按钮
            materialButton.isEnabled = true

            Timber.d("Datagram send to turn on/off cooler:$coolerCommand")
        }


        // 设置温度滑动条的监听器
        binding.sbTemperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var temperature: Int = 15
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Called when the progress value is changed
                temperature = 15 + (progress * 15) / 100

                consoleViewModel.setTargetTemperature(temperature)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Called when the user starts touching the seekbar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Called when the user stops touching the seekbar
            }
        })


        /// ---------------------------- 控制湿度部分 --------------------------------------------------------

        // 自动湿度控制打开按钮监听
        binding.switchHumidity.setOnCheckedChangeListener { compoundButton, b ->
            // 禁用 Switch 防止用户在执行过程中改变其状态
            compoundButton.isEnabled = false

            // 根据布尔值b决定操作和备注信息
            val action = if (b) "enable" else "disable"
            val remark =
                if (b) "Command to enable the auto control target humidity" else "Command to disable the auto control target humidity"

            // 构建命令数据报
            val humidityCommand = CommandDatagram.Builder()
                .setCommand("Humidity-Control")
                .setAction(action)
                .setRemark(remark)
                .setTarget(consoleViewModel.targetHumidity.value!!)
                .build()

            // 转换为JSON字符串并发送
            val datagram = humidityCommand.toJsonString()
            consoleViewModel.sendData(datagram)

            // 启用 Switch
            compoundButton.isEnabled = true

            Timber.d("Datagram send to auto control humidity:$humidityCommand")
        }


        // 设置湿度输入框的监听器,检测输入湿度
        binding.etHumiditySetting.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // This method is called when the text is changing
            }

            override fun afterTextChanged(editable: Editable) {
                // This method is called after the text has changed

                // Convert text to integer and update data
                val newHumidityText = editable.toString()
                val trimmedText = newHumidityText.replace("%", "").trim()
                if (trimmedText.isNotEmpty()) {
                    val newHumidity = Integer.parseInt(trimmedText)
                    consoleViewModel.setTargetHumidity(newHumidity)
                }

            }
        })
        binding.etHumiditySetting.setOnFocusChangeListener { view, b ->
            if (!b) {
                // 当EditText失去焦点时，隐藏键盘
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etHumiditySetting.windowToken, 0)
            }
        }


        // 加湿控制
        binding.segmentedButtonHumidityIncrease.addOnCheckedChangeListener { materialButton, b ->
            // 禁用 按钮 防止用户在执行过程中改变其状态
            materialButton.isEnabled = false

            // 根据布尔值b决定操作和备注信息
            val action = if (b) "turn_on" else "turn_off"
            val remark =
                if (b) "Command to turn on the humidifier." else "Command to turn off the humidifier."

            // 构建命令数据报
            val humidifierCommand = CommandDatagram.Builder()
                .setCommand("Actuator-Control")
                .setAction(action)
                .setRemark(remark)
                .setActuator("Humidifier")
                .build()

            // 转换为JSON字符串并发送
            val datagram = humidifierCommand.toJsonString()
            consoleViewModel.sendData(datagram)

            // 重新启用 按钮
            materialButton.isEnabled = true

            Timber.d("Datagram send to turn on/off humidifier:$humidifierCommand")
        }


        // 除湿控制
        binding.segmentedButtonHumidityDecrease.addOnCheckedChangeListener { materialButton, b ->
            // 禁用 按钮 防止用户在执行过程中改变其状态
            materialButton.isEnabled = false

            // 根据布尔值b决定操作和备注信息
            val action = if (b) "turn_on" else "turn_off"
            val remark =
                if (b) "Command to turn on the dehumidifier." else "Command to turn off the dehumidifier."

            // 构建命令数据报
            val dehumidifierCommand = CommandDatagram.Builder()
                .setCommand("Actuator-Control")
                .setAction(action)
                .setRemark(remark)
                .setTarget(255)
                .setActuator("Dehumidifier")
                .build()

            // 转换为JSON字符串并发送
            val datagram = dehumidifierCommand.toJsonString()
            consoleViewModel.sendData(datagram)

            // 重新启用 按钮
            materialButton.isEnabled = true

            Timber.d("Datagram send to turn on/off dehumidifier:$dehumidifierCommand")
        }


        /// ---------------------------- 控制灯光部分 --------------------------------------------------------

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

            Timber.d("Datagram send to switch on/off light:$lightCommand")
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

            Timber.d("Datagram send to auto control light:$lightControlCommand")
        }


        // 查询数据库,使用观察者模式观察更改
        // 观察 LiveData
        weatherDao.getLatestWeatherData().observe(viewLifecycleOwner, Observer { latestWeather ->
            if (latestWeather != null) {
                Timber.d("Database has been found,weather condition: ${latestWeather.weatherCondition}")
                // 将其存储
                consoleViewModel.setWeatherDetail(latestWeather.let {
                    DataTransferObject.toUsefulDailyWeatherDetail(
                        it
                    )
                })
            } else {
                Timber.d("Database: No weather data found")
            }
        })
    }


    // 将数据更新到RoomDatabase
    suspend fun updateWeatherRoomDatabase(weatherDetail: UsefulDailyWeatherDetail) {
        withContext(Dispatchers.IO) {
            // 存储到Room数据库
            // 更新数据库中的天气数据
            val weatherEntity =
                DataTransferObject.toWeatherEntity(weatherDetail)

            weatherDao.insertSingleWeather(weatherEntity)
            // 确认对象
            Timber.d("Database: ")
            Timber.d(
                SmartWardrobeDatabase.getInstance(requireActivity().application)
                    .toString()
            )
        }
    }

    // 从RoomDatabase查询数据
    private suspend fun queryWeatherRoomDatabase() {
        return withContext(Dispatchers.IO) {
            // 查询 Room 数据库
            val weatherEntity = weatherDao.getLatestWeatherData()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}