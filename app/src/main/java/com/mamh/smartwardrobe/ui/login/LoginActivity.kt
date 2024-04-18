package com.mamh.smartwardrobe.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mamh.smartwardrobe.R
import com.mamh.smartwardrobe.databinding.ActivityLoginBinding
import com.mamh.smartwardrobe.ui.main.MainActivity

// 主Activity类用于处理登录
class LoginActivity : AppCompatActivity() {

    // 定义ViewModel和视图绑定对象
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    // Activity的创建时的初始化过程
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置视图绑定
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 从视图绑定中获取UI组件
        val username = binding.editUsername
        val password = binding.editPassword
        val login = binding.btnLogin
        val touristMode = binding.btnTouristMode

        // 初始化ViewModel
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        // 观察登录表单状态LiveData
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // 根据表单状态启用或禁用登录按钮
            login?.isEnabled = loginState.isDataValid

            // 显示用户名和密码的错误信息
            if (loginState.usernameError != null) {
                username?.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password?.error = getString(loginState.passwordError)
            }
        })

        // 观察登录结果,一旦登录结果有改变，则马上跳转成功登录后的界面，转MainActivity
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error) // 显示登录失败信息
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success) // 更新UI显示成功信息
            }
            setResult(Activity.RESULT_OK)

            // 登录成功后关闭Activity
            finish()
        })

        // 用户名文本变更监听
        username?.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password?.text.toString()
            )
        }

        // 密码输入框的处理
        password?.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username?.text.toString(),
                    password.text.toString()
                )
            }

            // 密码输入后点击键盘上的完成（Done）按钮，触发登录
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username?.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            // 登录按钮点击事件处理，显示加载指示器并触发登录逻辑
            login?.setOnClickListener {
                loginViewModel.login(username?.text.toString(), password.text.toString())
            }
        }

        // 如果是体验模式，也可以直接按下按钮即可进入程序
        touristMode?.setOnClickListener {
            // 显示加载指示器
            binding.loading?.visibility = View.VISIBLE

            // 假设游客模式使用特定的用户名和密码，这里使用空字符串或预定义的游客账户
            val guestUsername = "guest"
            val guestPassword = "password"

            // 调用ViewModel的login方法进行登录
            loginViewModel.login(guestUsername, guestPassword)
        }
    }

    // 更新UI显示欢迎信息
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        // 跳转到MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // 显示登录失败的Toast
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

// 扩展函数：EditText的文本变更后处理逻辑简化
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
