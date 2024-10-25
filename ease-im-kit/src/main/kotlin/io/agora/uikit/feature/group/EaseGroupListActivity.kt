package io.agora.uikit.feature.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import io.agora.uikit.EaseIM
import io.agora.uikit.base.EaseBaseActivity
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.databinding.EaseLayoutGroupListBinding
import io.agora.uikit.feature.group.fragments.EaseGroupListFragment

class EaseGroupListActivity: EaseBaseActivity<EaseLayoutGroupListBinding>() {
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