package com.qxtao.easyenglish.ui.fragment.register

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private var _timer = MutableLiveData<CountDownTimer>()
    var timeLeft = MutableLiveData<Long>()
    var isShowPassword = MutableLiveData<Boolean>()
    var isShowCheckPassword = MutableLiveData<Boolean>()
    var isEmailError = MutableLiveData<Boolean>()
    var isPasswordError = MutableLiveData<Boolean>()
    var isCheckPasswordError = MutableLiveData<Boolean>()

    init {
        timeLeft.value = 0L
        isShowPassword.value = false
        isShowCheckPassword.value = false
        isEmailError.value = false
        isPasswordError.value = false
        isCheckPasswordError.value = false
    }

    fun setTimer(millisInFuture: Long, countDownInterval: Long){
        _timer.value = CountDownTime(millisInFuture,countDownInterval)
        (_timer.value as CountDownTime).start()
    }

    private inner class CountDownTime(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            timeLeft.value = (millisUntilFinished / 1000)
        }
        override fun onFinish() {
            timeLeft.value = 0
        }
    }

}
