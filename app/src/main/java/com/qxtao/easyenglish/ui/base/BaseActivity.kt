@file:Suppress("MemberVisibilityCanBePrivate")

package com.qxtao.easyenglish.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.utils.factory.isNotSystemInDarkMode
import org.greenrobot.eventbus.EventBus


abstract class BaseActivity<VB : ViewBinding>(open val block:(LayoutInflater)->VB) : AppCompatActivity(), View.OnClickListener {

    protected val binding by lazy{ block(layoutInflater) }
    private lateinit var _context: Context
    protected val mContext get() = _context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Display SplashScreen
         * 展示开屏动画
         */
        if (isDisplaySplashScreen()) { installSplashScreen() }
        setContentView(binding.root)
        /**
         * Hide Activity title bar
         * 隐藏系统的标题栏
         */
        supportActionBar?.hide()
        /**
         * Init immersive status bar
         * 初始化沉浸状态栏
         */
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = isNotSystemInDarkMode
            isAppearanceLightNavigationBars = isNotSystemInDarkMode
        }
        /**
         * set Status color
         * 设置状态颜色
         */
        setStatusColor(
            ResourcesCompat.getColor(resources, R.color.colorThemeBackground, null),
            ResourcesCompat.getColor(resources, R.color.colorThemeBackground, null),
            ResourcesCompat.getColor(resources, R.color.colorThemeBackground, null)
        )
        /**
         * Init children
         * 装载子类
         */
        onCreate()
        /**
         * Bind EventBus
         * 注册EventBus
         */
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this)
            Log.e("eventBus", "register")
        }
        _context = this
        bindViews()
        initViews()
        initEvents()
        addListener()
    }


    /**
     * Callback [onCreate] method
     *
     * 回调 [onCreate] 方法
     */
    abstract fun onCreate()

    /**
     * isDisplaySplashScreen
     *
     * SplashScreen 开屏动画
     */
    protected open fun isDisplaySplashScreen(): Boolean { return false }

    /**
     * setStatusColor
     *
     * 设置状态颜色
     */
    protected fun setStatusColor(statusBarColor: Int, navigationBarColor: Int, navigationBarDividerColor: Int) {
        setStatusBarColor(statusBarColor)
        setNavigationBarColor(navigationBarColor)
        setNavigationBarDividerColor(navigationBarDividerColor)
    }
    protected open fun setStatusBarColor(statusBarColor: Int) { window?.statusBarColor = statusBarColor }
    protected open fun setNavigationBarColor(navigationBarColor: Int) { window?.navigationBarColor = navigationBarColor }
    protected open fun setNavigationBarDividerColor(navigationBarDividerColor: Int) { window?.navigationBarDividerColor = navigationBarDividerColor }

    /**
     * Callback [bindViews] method
     *
     * 回调 [bindViews] 方法
     */
    protected open fun bindViews(){}

    /**
     * Callback [initViews] method
     *
     * 回调 [initViews] 方法
     */
    protected abstract fun initViews()

    /**
     * Callback [initEvents] method
     *
     * 回调 [initEvents] 方法
     */
    protected open fun initEvents(){}

    /**
     * Callback [addListener] method
     *
     * 回调 [addListener] 方法
     */
    protected abstract fun addListener()

    /**
     * is bind eventbus
     *
     * 是否绑定eventbus事件
     */
    protected open fun isBindEventBusHere(): Boolean { return false }

    /**
     * show short toast
     *
     * Toast通知
     */
    protected fun showShortToast(str: String) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show()
    }

    /**
     * show long toast
     *
     * Toast通知
     */
    protected fun showLongToast(str: String) {
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show()
    }

    /**
     * Block problems caused by quick clicks
     * 屏蔽快速点击而引发的问题
     */
    protected open fun onWidgetClick(view: View?) {}
    private var lastTime = 0L
    private var currentView: View? = null
    override fun onClick(view: View) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - lastTime > 1000L || view.id == currentView!!.id) {
            lastTime = nowTime
            currentView = view
            onWidgetClick(view)
        }
    }

    override fun onDestroy() {
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this)
            Log.e("eventBus", "unregister")
        }
        super.onDestroy()
    }

}
