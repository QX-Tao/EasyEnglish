package com.qxtao.easyenglish.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.qxtao.easyenglish.utils.constant.NetConstant
import com.xuexiang.xhttp2.XHttpSDK


class DefaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * 网络请求框架
         * XHttp: network request framework
         */
        XHttpSDK.init(this)

        /**
         * 网络请求的基础地址
         * XHttp: base url
         */
        XHttpSDK.setBaseUrl(NetConstant.baseService)
        XHttpSDK.debug("XHttp")

        /**
         * 主题模式
         * theme mode setting
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        /**
         * 语言模式
         * language mode setting
         */

    }

}