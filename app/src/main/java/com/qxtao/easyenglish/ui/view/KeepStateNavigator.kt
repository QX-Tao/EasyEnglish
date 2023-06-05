package com.qxtao.easyenglish.ui.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

/**
 * @author: PanYiTao
 * @date: 2022/08/16 2:27
 * @description: 自定义 Navigation，使在 fragment 间切换不会重载
 */
@Navigator.Name("keep_state_fragment")
class KeepStateNavigator(
    private val context: Context,
    private val manager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val tag = destination.id.toString()
        val transaction = manager.beginTransaction()

        var initialNavigate = false
        val currentFragment = manager.primaryNavigationFragment
        if (currentFragment != null) {
//            transaction.detach(currentFragment)
            transaction.hide(currentFragment)
        } else {
            initialNavigate = true
        }

        var fragment = manager.findFragmentByTag(tag)
        if (fragment == null) {
            val className = destination.className
            fragment = manager.fragmentFactory.instantiate(context.classLoader, className)
            transaction.add(containerId, fragment, tag)
        } else {
//            transaction.attach(fragment)
            transaction.show(fragment)
        }

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)
        transaction.commitNow()

        return if (initialNavigate) destination else null
    }
}
