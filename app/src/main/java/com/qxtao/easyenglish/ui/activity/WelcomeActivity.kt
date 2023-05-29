package com.qxtao.easyenglish.ui.activity

import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.databinding.ActivityWelcomeBinding
import com.qxtao.easyenglish.ui.base.BaseActivity
import com.qxtao.easyenglish.ui.base.BaseFragment
import com.qxtao.easyenglish.ui.fragment.forgotpassword.ForgotPasswordFragment
import com.qxtao.easyenglish.ui.fragment.login.LoginFragment
import com.qxtao.easyenglish.ui.fragment.register.RegisterFragment
import java.util.*
import kotlin.concurrent.schedule

class WelcomeActivity: BaseActivity<ActivityWelcomeBinding>(ActivityWelcomeBinding::inflate),
    BaseFragment.OnFragmentInteractionListener {

    companion object{
        private var isForgotPasswordFragmentDisplay = false
        private var isReady = false
    }

    private val welcomePagerAdapter by lazy { WelcomePagerAdapter(this) }

    override fun onCreate() {
        Timer().schedule(500){ isReady = true }
        displaySplash()
        binding.llLoginPanelViewPager.adapter = welcomePagerAdapter
        TabLayoutMediator(binding.llLoginPanelTabLayout, binding.llLoginPanelViewPager){ tab, position ->
            when(position){
                0 -> tab.text = getString(R.string.login)
                1 -> tab.text = getString(R.string.register)
                2 -> tab.text = getString(R.string.forgot_password)
            }
        }.attach()
        binding.llLoginPanelTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val animatorDurationScale: Float = Settings.Global.getFloat(
                    contentResolver,
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f
                )
                if (tab != null) {
                    when(tab.position){
                        2 -> {
                            binding.llLoginPanelTabLayout.getTabAt(2)?.view?.isClickable = false
                            isForgotPasswordFragmentDisplay = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                welcomePagerAdapter.notifyItemChanged(2)
                            }, (animatorDurationScale * 400).toLong())
                        }
                    }
                }
            }
        })




    }


    override fun isDisplaySplashScreen(): Boolean { return true }
    override fun initViews() {}
    override fun addListener() {}

    /**
     * display SplashScreen
     * 开屏动画
     */
    private fun displaySplash(){
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    /**
     * method onFragmentInteraction
     * @param data String?
     */
    override fun onFragmentInteraction(data: String?) {
        when(data){
            "toMainActivity" -> { toMainActivity() }
            "toRegisterFragment" -> { toRegisterFragment() }
            "toForgotPasswordFragment" -> { toForgotPasswordFragment() }
            "setNewPassword" -> { setNewPassword() }
            "getVerCode" -> { getVerCode() }
            "onRegister" -> { onRegister() }
        }
    }

    private fun toMainActivity() {
        goAndFinish(MainActivity::class.java)
    }

    private fun toRegisterFragment() {
        binding.llLoginPanelViewPager.currentItem = 1
    }

    private fun setNewPassword() {}

    private fun getVerCode() {}

    private fun onRegister() {}

    private fun toForgotPasswordFragment() {
        isForgotPasswordFragmentDisplay = true
        welcomePagerAdapter.notifyItemChanged(2)
        Handler(Looper.getMainLooper()).post { binding.llLoginPanelViewPager.currentItem = 2 }
    }



    /**
     * Hidden soft input
     * 隐藏软键盘
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (isHideInput(view, ev)) {
                hideSoftInput(view!!.windowToken)
                view.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    private fun isHideInput(v: View?, ev: MotionEvent): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return ev.x <= left || ev.x >= right || ev.y <= top || ev.y >= bottom
        }
        return false
    }
    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * PagerAdapter Class
     * @constructor
     */
    class WelcomePagerAdapter(fActivity: FragmentActivity) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fActivity) {
        override fun getItemCount() = when(isForgotPasswordFragmentDisplay){
            true -> 3
            false -> 2
        }
        override fun createFragment(position: Int) = when(position){
            0 -> LoginFragment()
            1 -> RegisterFragment()
            2 -> ForgotPasswordFragment()
            else -> LoginFragment()
        }
    }
}





