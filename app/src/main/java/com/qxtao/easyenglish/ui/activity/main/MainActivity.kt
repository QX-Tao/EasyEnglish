package com.qxtao.easyenglish.ui.activity.main

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.databinding.ActivityMainBinding
import com.qxtao.easyenglish.ui.base.BaseActivity
import com.qxtao.easyenglish.ui.base.BaseFragment
import com.qxtao.easyenglish.ui.fragment.word.WordFragment
import com.qxtao.easyenglish.ui.view.KeepStateNavigator

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    BaseFragment.OnFragmentInteractionListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var manager: FragmentManager
    private lateinit var navView: BottomNavigationView
    private var exitTime: Long = 0

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

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val currentFragment = manager.primaryNavigationFragment
                if(currentFragment is WordFragment){
                    if (System.currentTimeMillis() - exitTime < 2000) {
                        exitTime = 0
                        finish()
                    } else {
                        showShortToast(getString(R.string.press_back_again_to_exit))
                        exitTime = System.currentTimeMillis()
                    }
                } else {
                    navView.selectedItemId = R.id.navigation_word
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

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
     * method onFragmentInteraction
     * @param data String?
     */
    override fun onFragmentInteraction(vararg data: String?) {

    }

}