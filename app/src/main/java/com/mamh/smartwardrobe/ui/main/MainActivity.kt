package com.mamh.smartwardrobe.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.mamh.smartwardrobe.R
import com.mamh.smartwardrobe.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    //部分live data变量更新次数统计，初始为0，每次使用+1(防止bug)
    var usageCount: Int = 0
    var usageCount1: Int = 0


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // private lateinit var dataSendBinding: DialogDataSendBinding fixme
    private val viewModel: MainActivityViewModel by viewModels()

    private val REQUEST_ACCESS_NETWORK_STATE = 0
    private val REQUEST_INTERNET_STATE = 1
    private val REQUEST_ACCESS_WIFI_STATE = 2
    private val REQUEST_ACCESS_FINE_LOCATION = 3
    private val REQUEST_READ_PHONE_STATE = 4
    private val REQUEST_CHANGE_WIFI_STATE = 5

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //请求权限，处理权限问题
        permissionRequest()

        //初始化视图布局
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        setListener()


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_console, R.id.nav_datacenter
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    //设置点击事件和live data的监听
    @OptIn(DelicateCoroutinesApi::class)
    private fun setListener() {

    }

    //点击+号fab，发送数据的界面
    private fun showDataSendingDetail() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return true
    }


    private fun permissionRequest() {
        //申请网络状态权限
        val permissionNetworkState =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
        //获取wifi状态
        val permissionWifiAccess =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
        //获取精确定位权限，这样才能获取到附近的wifi
        val permissionFineLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        //读取手机状态
        val permissionReadPhoneState =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        //修改wifi权限，允许app让设备连接到wifi
        val permissionChangeWifiAccess =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)

        //索要网络权限
        if (permissionNetworkState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                REQUEST_INTERNET_STATE
            )
        } else {
            Timber.d("已授予互联网权限")
        }

        //索要wifi权限
        if (permissionWifiAccess != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
                REQUEST_ACCESS_WIFI_STATE
            )
        } else {
            Timber.d("已授予wifi权限")
        }

        //索要精度定位权限
        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            Timber.d("已授予高精度定位权限")
        }

        //索要读取手机状态权限
        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_READ_PHONE_STATE
            )
        } else {
            Timber.d("已授予读取手机状态权限")
        }

        //索要修改wifi连接权限
        if (permissionChangeWifiAccess != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CHANGE_WIFI_STATE),
                REQUEST_CHANGE_WIFI_STATE
            )
        } else {
            Timber.d("已授予修改wifi连接权限")
        }
    }

    //权限请求和授予
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_INTERNET_STATE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("网络权限已经正常授予")
            }

            REQUEST_ACCESS_WIFI_STATE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("wifi权限已经正常授予")
            }

            REQUEST_ACCESS_FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("精确定位权限已经正常授予")
            }

            REQUEST_READ_PHONE_STATE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("读取手机状态权限已经正常授予")
            }

            REQUEST_CHANGE_WIFI_STATE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("连接特定wifi权限已经正常授予")
            }

            else -> {
                Toast.makeText(this, "请授权软件网络权限和位置信息权限", Toast.LENGTH_SHORT).show()
            }
        }
    }
}