package com.hyphenate.easeui.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.showToast
import com.hyphenate.easeui.demo.base.BaseInitActivity
import com.hyphenate.easeui.demo.databinding.ActivityMainBinding
import com.hyphenate.easeui.demo.login.AboutMeFragment
import com.hyphenate.easeui.feature.contact.EaseContactsListFragment
import com.hyphenate.easeui.feature.conversation.EaseConversationListFragment
import com.hyphenate.easeui.interfaces.OnEventResultListener


class MainActivity : BaseInitActivity<ActivityMainBinding>(), NavigationBarView.OnItemSelectedListener,
    OnEventResultListener {
    override fun getViewBinding(inflater: LayoutInflater): ActivityMainBinding? {
        return ActivityMainBinding.inflate(inflater)
    }

    private var mConversationListFragment: Fragment? = null
    private var mContactFragment:Fragment? = null
    private var mAboutMeFragment:Fragment? = null
    private var mCurrentFragment: Fragment? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.navView.itemIconTintList = null
        switchToHome()
        checkIfShowSavedFragment(savedInstanceState)
    }

    override fun initListener() {
        super.initListener()
        binding.navView.setOnItemSelectedListener(this)
        EaseIM.addEventResultListener(this)
    }

    private fun switchToHome() {
        if (mConversationListFragment == null) {
            mConversationListFragment = EaseConversationListFragment.Builder()
                .useTitleBar(true)
                .enableTitleBarPressBack(false)
                .useSearchBar(true)
                .build()
        }
        mConversationListFragment?.let {
            replace(it, "conversation")
        }
    }

    private fun switchToContacts() {
        if (mContactFragment == null) {
            mContactFragment = EaseContactsListFragment.Builder()
                .useTitleBar(true)
                .useSearchBar(true)
                .enableTitleBarPressBack(false)
                .setHeaderItemVisible(true)
                .build()
        }
        mContactFragment?.let {
            replace(it, "contact")
        }
    }

    private fun switchToAboutMe() {
        if (mAboutMeFragment == null) {
            mAboutMeFragment = AboutMeFragment()
        }
        mAboutMeFragment?.let {
            replace(it, "me")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EaseIM.removeEventResultListener(this)
    }

    private fun replace(fragment: Fragment, tag: String) {
        if (mCurrentFragment !== fragment) {
            val t = supportFragmentManager.beginTransaction()
            mCurrentFragment?.let {
                t.hide(it)
            }
            mCurrentFragment = fragment
            if (!fragment.isAdded) {
                t.add(R.id.fl_main_fragment, fragment, tag).show(fragment).commit()
            } else {
                t.show(fragment).commit()
            }
        }
    }

    /**
     * 用于展示是否已经存在的Fragment
     * @param savedInstanceState
     */
    private fun checkIfShowSavedFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val tag = savedInstanceState.getString("tag")
            if (!tag.isNullOrEmpty()) {
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment is Fragment) {
                    Log.e("MainActivity", "checkIfShowSavedFragment: $tag")
                    replace(fragment, tag)
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var showNavigation = false
        when (item.itemId) {
            R.id.em_main_nav_home -> {
                switchToHome()
                showNavigation = true
            }

            R.id.em_main_nav_friends -> {
                switchToContacts()
                showNavigation = true
            }

            R.id.em_main_nav_me -> {
                switchToAboutMe()
                showNavigation = true
            }
        }
        invalidateOptionsMenu()
        return showNavigation
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mCurrentFragment != null) {
            outState.putString("tag", mCurrentFragment!!.tag)
        }
    }

    companion object {
        fun actionStart(context: Context) {
            Intent(context, MainActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }

    override fun onEventResult(function: String, errorCode: Int, errorMessage: String?) {
        when(function){
            EaseConstant.API_ASYNC_ADD_CONTACT -> {
                if (errorCode == ChatError.EM_NO_ERROR){
                    runOnUiThread{
                        mContext.showToast(mContext.resources.getString(R.string.em_main_add_contact_success))
                    }
                }else{
                    runOnUiThread{
                        mContext.showToast(mContext.resources.getString(R.string.em_main_add_contact_fail,errorMessage))
                    }
                }
            }
            else -> {}
        }
    }

}


