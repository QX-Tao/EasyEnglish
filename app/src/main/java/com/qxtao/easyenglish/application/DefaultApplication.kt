package com.qxtao.easyenglish.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class DefaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()

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