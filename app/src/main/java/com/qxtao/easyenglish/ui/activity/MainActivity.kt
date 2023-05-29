package com.qxtao.easyenglish.ui.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.databinding.ActivityMainBinding
import com.qxtao.easyenglish.ui.base.BaseActivity
import com.qxtao.easyenglish.ui.fragment.word.WordFragment
import com.qxtao.easyenglish.ui.base.BaseFragment
import com.qxtao.easyenglish.ui.view.KeepStateNavigator

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    BaseFragment.OnFragmentInteractionListener {

    private lateinit var manager: FragmentManager
    private lateinit var wordFragment: Fragment
    private lateinit var navView: BottomNavigationView
    private var exitTime: Long = 0
    private var needGetWordFragment: Boolean = true

    override fun onCreate() {

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment?
        val navigator = KeepStateNavigator(
            this,
            navHostFragment!!.childFragmentManager,
            R.id.nav_host_fragment_activity_main
        )
        navController.navigatorProvider.addNavigator(navigator)
        navController.setGraph(R.navigation.mobile_navigation)
        setUpNavBottom(navHostFragment)

        navView = binding.navView
        manager = navHostFragment.childFragmentManager



    }

    override fun initViews() {}
    override fun addListener() {}


    /**
     * 设置底部导航栏
     * SetUp BottomNavigation
     */
    private fun setUpNavBottom(hostFragment: NavHostFragment?) {
        val navMenu = findViewById<BottomNavigationView>(R.id.nav_view)
        if (hostFragment != null) {
            val navController = hostFragment.navController
            setupWithNavController(navMenu, navController)
        }
    }

    /**
     * 双击退出应用
     * Double click to exit
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = manager.primaryNavigationFragment
        // 当面界面不是mapFragment，则返回mapFragment
        if (wordFragment != currentFragment) {
            navView.selectedItemId = R.id.navigation_word
        } else {
            if (wordFragment is WordFragment) {
                if ((wordFragment as WordFragment).onBackPressed()) {
                    return  // 在Fragment中处理返回逻辑
                }
            }
            if (System.currentTimeMillis() - exitTime < 2000) {
                exitTime = 0
                finish()
            } else {
                showShortToast(getString(R.string.press_back_again_to_exit))
                exitTime = System.currentTimeMillis()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (needGetWordFragment) {
            wordFragment = manager.primaryNavigationFragment!!
            needGetWordFragment = false
        }
    }

    override fun onFragmentInteraction(data: String?) {

    }

}