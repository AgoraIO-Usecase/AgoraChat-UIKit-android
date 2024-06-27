package com.hyphenate.easeui.feature.chat.pin.holder

import android.annotation.SuppressLint
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.extensions.getMessageDigest
import com.hyphenate.easeui.databinding.EasePinlistDefaultLayoutBinding
import com.hyphenate.easeui.provider.getSyncUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class EaseChatPinDefaultViewHolder(
    private val mItemSubViewListener: EaseBaseRecyclerViewAdapter.OnItemSubViewClickListener?,
    val viewBinding: EasePinlistDefaultLayoutBinding
): EaseBaseRecyclerViewAdapter.ViewHolder<ChatMessage>(binding = viewBinding) {
    private var isConfirm:Boolean = false
    @SuppressLint("SetTextI18n")
    override fun setData(item: ChatMessage?, position: Int) {
        viewBinding.run {
            item?.let { message->
                isConfirm = false
                tvState.text = itemView.context.getString(R.string.ease_pin_list_item_delete)
                val operatorId = message.pinnedInfo()?.operatorId()
                val pinTime = message.pinnedInfo().pinTime()
                val operatorUserInfo = EaseIM.getUserProvider()?.getSyncUser(operatorId)
                val fromUserInfo = EaseIM.getUserProvider()?.getSyncUser(message.from)

                tvFrom.text = "${operatorUserInfo?.getRemarkOrName()?:kotlin.run { operatorId }} " +
                        "pinned ${fromUserInfo?.getRemarkOrName()?:kotlin.run { message.from }} 's message"

                tvContent.text = message.getMessageDigest(itemView.context)

                val sdf = SimpleDateFormat("MM-dd, HH:mm")
                sdf.timeZone = TimeZone.getTimeZone("GMT+8")
                val formattedDate = sdf.format(Date(pinTime))
                tvTime.text = formattedDate
                tvState.setOnClickListener { v ->
                    if (isConfirm){
                        mItemSubViewListener?.onItemSubViewClick(v, position)
                    }else{
                        tvState.text = itemView.context.getString(R.string.ease_pin_list_item_confirm_delete)
                    }
                    isConfirm = true
                }
            }
        }
    }

}