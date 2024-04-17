package com.mamh.smartwardrobe.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mamh.smartwardrobe.R
import com.mamh.smartwardrobe.data.LoginRepository
import com.mamh.smartwardrobe.data.Result

// 登录ViewModel，负责处理登录逻辑和表单验证
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    // 私有的MutableLiveData，用于更新登录表单状态
    private val _loginForm = MutableLiveData<LoginFormState>()
    // 对外暴露不可变的LiveData，用于观察登录表单状态
    val loginFormState: LiveData<LoginFormState> = _loginForm

    // 私有的MutableLiveData，用于存储登录结果
    private val _loginResult = MutableLiveData<LoginResult>()
    // 对外暴露不可变的LiveData，用于观察登录结果
    val loginResult: LiveData<LoginResult> = _loginResult

    // 处理用户登录的函数
    fun login(username: String, password: String) {
        // 异步启动登录过程
        val result = loginRepository.login(username, password)

        // 处理登录结果
        if (result is Result.Success) {
            // 登录成功，更新LiveData状态，传递用户显示名
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            // 登录失败，更新LiveData状态，显示失败信息
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    // 检查登录表单数据的变化，验证用户名和密码
    fun loginDataChanged(username: String, password: String) {
        // 验证用户名是否有效
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            // 验证密码是否有效
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            // 用户名和密码都有效，更新表单状态为有效
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // 验证用户名是否有效的辅助函数
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            // 如果用户名包含@，认为是电子邮件地址，进一步使用正则表达式验证
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            // 如果不包含@，只需检查是否为空
            username.isNotBlank()
        }
    }

    // 验证密码是否有效的辅助函数
    private fun isPasswordValid(password: String): Boolean {
        // 密码长度需要超过5位
        return password.length > 5
    }
}
