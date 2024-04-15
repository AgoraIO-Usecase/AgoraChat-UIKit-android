package com.hyphenate.easeui.feature.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.EaseLayoutGroupListBinding
import com.hyphenate.easeui.feature.group.fragment.EaseGroupListFragment

class EaseGroupListActivity:EaseBaseActivity<EaseLayoutGroupListBinding>() {
    override fun getViewBinding(inflater: LayoutInflater): EaseLayoutGroupListBinding {
        return EaseLayoutGroupListBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EaseGroupListFragment().let {
            supportFragmentManager.beginTransaction().add(binding.root.id, it).commit()
        }

    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, EaseGroupListActivity::class.java)
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    context.startActivity(it)
                    return
                }
            }
            context.startActivity(intent)
        }
    }


}