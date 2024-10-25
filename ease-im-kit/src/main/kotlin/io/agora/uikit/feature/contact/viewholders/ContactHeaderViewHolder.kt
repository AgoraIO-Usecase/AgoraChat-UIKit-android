package io.agora.uikit.feature.contact.viewholders

import android.view.View
import androidx.viewbinding.ViewBinding
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.configs.setAvatarStyle
import io.agora.uikit.databinding.EaseLayoutItemHeaderBinding
import io.agora.uikit.feature.contact.config.EaseContactHeaderConfig
import io.agora.uikit.feature.contact.config.bindView
import io.agora.uikit.model.EaseCustomHeaderItem

class ContactHeaderViewHolder(
    private val viewBinding: EaseLayoutItemHeaderBinding,
    val config:EaseContactHeaderConfig? = EaseContactHeaderConfig()
): EaseBaseRecyclerViewAdapter.ViewHolder<EaseCustomHeaderItem>(binding = viewBinding)  {

    init {
        config?.bindView(viewBinding)
    }

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        if(viewBinding is EaseLayoutItemHeaderBinding){
            EaseIM.getConfig()?.avatarConfig?.setAvatarStyle(viewBinding.headerItem.avatar)
        }
    }

    override fun setData(item: EaseCustomHeaderItem?, position: Int) {
        val headerItem = viewBinding.headerItem
        headerItem.tvTitle?.text = item?.headerTitle ?: ""
        headerItem.tvContent?.text = item?.headerContent ?: ""

        if (item?.headerIconRes != -1 ){
            item?.headerIconRes?.let {
                headerItem.setAvatarVisibility(View.VISIBLE)
                headerItem.setAvatar(it)
            }
        }
        if (item?.headerEndIconRes != -1){
            item?.headerEndIconRes?.let {
                headerItem.setArrow(it)
            }
        }

        if (item?.headerItemDivider == true){
            headerItem.setItemDividerVisibility(View.VISIBLE)
        }else{
            headerItem.setItemDividerVisibility(View.GONE)
        }

        if (item?.headerItemShowArrow == true){
            headerItem.setArrowVisibility(View.VISIBLE)
        }else{
            headerItem.setArrowVisibility(View.GONE)
        }

        if (item?.headerUnReadCount == 0){
            viewBinding.unreadCount.text = ""
            viewBinding.rlUnreadRight.visibility = View.GONE
        }else{
            viewBinding.unreadCount.text = item?.headerUnReadCount.toString()
            item?.headerUnReadCount?.let {
                if (it > 99){
                    val context = EaseIM.getContext()
                    context?.let { con->
                        viewBinding.unreadCount.text = con.resources.getString(R.string.ease_message_unread_count_max)
                    }
                }
            }
            viewBinding.rlUnreadRight.visibility = View.VISIBLE
        }
    }

}