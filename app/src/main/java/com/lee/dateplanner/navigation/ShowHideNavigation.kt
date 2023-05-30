package com.lee.dateplanner.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("show_hide_navigation_fragment")
class ShowHideNavigation(private val context: Context, private val manager: FragmentManager, private val containerId: Int)
    : FragmentNavigator(context, manager, containerId){

    override fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val tag = destination.id.toString()
        val transaction = manager.beginTransaction()
        var initialNavigate = false
        val currentFragment = manager.primaryNavigationFragment

        // 재생성 방지문
        if(currentFragment != null){
            Log.e("","화면 숨김")
            transaction.hide(currentFragment)
        }
        else{
            initialNavigate = true
        }

        var fragment = manager.findFragmentByTag(tag) // 최초생성 fragment
        if(fragment == null){ // 생성된 적이 없다면
            val className = destination.className
            fragment = manager.fragmentFactory.instantiate(context.classLoader, className)
            Log.e("","화면 생성")
            transaction.add(containerId, fragment, tag)
        } else{
            Log.e("","화면 보임")
            transaction.show(fragment)
        } // 이미 생성된 fragment 면

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)
        transaction.commitNow()

        return if(initialNavigate){
            destination
        }
        else {
            null
        }
        //return super.navigate(destination, args, navOptions, navigatorExtras)
    }
}