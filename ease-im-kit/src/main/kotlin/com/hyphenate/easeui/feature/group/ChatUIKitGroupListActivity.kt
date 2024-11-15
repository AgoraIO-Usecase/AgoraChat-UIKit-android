package com.hyphenate.easeui.feature.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.base.ChatUIKitBaseActivity
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.UikitLayoutGroupListBinding
import com.hyphenate.easeui.feature.group.fragments.ChatUIKitGroupListFragment

class ChatUIKitGroupListActivity:ChatUIKitBaseActivity<UikitLayoutGroupListBinding>() {
    override fun getViewBinding(inflater: LayoutInflater): UikitLayoutGroupListBinding {
        return UikitLayoutGroupListBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChatUIKitGroupListFragment().let {
            supportFragmentManager.beginTransaction().add(binding.root.id, it).commit()
        }

    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, ChatUIKitGroupListActivity::class.java)
            ChatUIKitClient.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    context.startActivity(it)
                    return
                }
            }
            context.startActivity(intent)
        }
    }


}