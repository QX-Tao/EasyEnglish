package com.qxtao.easyenglish.ui.fragment.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var isShowPassword = MutableLiveData<Boolean>()
    var isEmailError = MutableLiveData<Boolean>()

    init {
        isShowPassword.value = false
        isEmailError.value = false
    }
}
