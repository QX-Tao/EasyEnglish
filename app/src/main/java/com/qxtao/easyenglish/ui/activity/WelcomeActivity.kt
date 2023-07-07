package com.qxtao.easyenglish.ui.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.databinding.ActivityWelcomeBinding
import com.qxtao.easyenglish.ui.activity.main.MainActivity
import com.qxtao.easyenglish.ui.base.BaseActivity
import com.qxtao.easyenglish.ui.base.BaseFragment
import com.qxtao.easyenglish.ui.fragment.forgotpassword.ForgotPasswordFragment
import com.qxtao.easyenglish.ui.fragment.login.LoginFragment
import com.qxtao.easyenglish.ui.fragment.register.RegisterFragment
import com.qxtao.easyenglish.utils.constant.NetConstant
import com.xuexiang.xhttp2.XHttp
import com.xuexiang.xhttp2.callback.SimpleCallBack
import com.xuexiang.xhttp2.exception.ApiException
import java.util.Timer
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

        window.setDecorFitsSystemWindows(false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        if(ScreenUtils.isLandscape()) {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        }

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
        binding.llLoginPanelViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

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
    override fun onFragmentInteraction(vararg data: String?) {
        if (data.isNotEmpty()){
            when(data[0]){
                "toRegisterFragment" -> { toRegisterFragment() }
                "toForgotPasswordFragment" -> { toForgotPasswordFragment() }
                "onRegister" -> { onRegister() }
                "toMainActivity" -> { toMainActivity() }
                "getVerCode" -> { getVerCode(data[1]) }
                "setNewPassword" -> { setNewPassword(data[1], data[2]) }
            }
        } else {
            Log.e(TAG,"onFragmentInteraction need at least one parameter.")
        }
    }

    private fun toMainActivity() {
        val intent = Intent(mContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toRegisterFragment() {
        binding.llLoginPanelViewPager.currentItem = 1
    }

    private fun toLoginFragment() {
        binding.llLoginPanelViewPager.currentItem = 0
    }

    private fun setNewPassword(email: String?, newPassword: String?) {
        XHttp.post(NetConstant.getOtpCodeURL)
            .params("email", email)
            .params("newPassword", newPassword)
            .syncRequest(false)
            .execute(object :SimpleCallBack<Any>(){
                override fun onSuccess(response: Any?) {
                    showSetNewPasswordSuccessDialog()
                }
                override fun onError(e: ApiException?) {
                    showShortToast(mContext.getString(R.string.request_url_exception))
                    Log.e(TAG,"error while setting newpassword" + e.toString())
                }
            }
        )
    }

    private fun getVerCode(email: String?) {
        XHttp.post(NetConstant.getOtpCodeURL)
            .params("email", email)
            .syncRequest(false)
            .execute(object :SimpleCallBack<Any>(){
                override fun onSuccess(response: Any?) {
                    showShortToast(mContext.getString(R.string.vercode_has_been_sent))
                }
                override fun onError(e: ApiException?) {
                    showShortToast(mContext.getString(R.string.request_url_exception))
                    Log.e(TAG,"error while getting VerCode" + e.toString())
                }
            }
        )
    }

    private fun onRegister() {}

    private fun toForgotPasswordFragment() {
        isForgotPasswordFragmentDisplay = true
        welcomePagerAdapter.notifyItemChanged(2)
        Handler(Looper.getMainLooper()).post { binding.llLoginPanelViewPager.currentItem = 2 }
    }

    private fun showSetNewPasswordSuccessDialog(){
        val dialog = AlertDialog.Builder(mContext)
            .setTitle(mContext.getString(R.string.password_has_been_updated))
            .setMessage(mContext.getString(R.string.password_has_been_updated_desc))
            .setCancelable(true)
            .setPositiveButton(mContext.getString(R.string.confirm)){ _, _ -> toLoginFragment() }
            .create()
        dialog.show()
    }




    /**
     * PagerAdapter Class
     * @constructor
     */
    private inner class WelcomePagerAdapter(fActivity: FragmentActivity) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fActivity) {
        override fun getItemCount() = if(isForgotPasswordFragmentDisplay) 3 else 2
        override fun createFragment(position: Int) = when(position){
            0 -> LoginFragment()
            1 -> RegisterFragment()
            2 -> ForgotPasswordFragment()
            else -> LoginFragment()
        }
    }

}





