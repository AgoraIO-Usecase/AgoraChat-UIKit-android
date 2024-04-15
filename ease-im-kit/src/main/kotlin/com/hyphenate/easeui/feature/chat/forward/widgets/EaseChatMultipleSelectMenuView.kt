package com.hyphenate.easeui.feature.chat.forward.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.common.extensions.plus
import com.hyphenate.easeui.databinding.EaseLayoutChatMessagesMultiSelectMenuBinding
import com.hyphenate.easeui.feature.chat.forward.helper.EaseChatMessageMultiSelectHelper
import com.hyphenate.easeui.feature.chat.interfaces.IChatTopExtendMenu
import com.hyphenate.easeui.feature.chat.interfaces.OnMultipleSelectChangeListener
import com.hyphenate.easeui.interfaces.OnMenuDismissListener
import com.hyphenate.easeui.interfaces.OnMultiSelectMenuListener
import com.hyphenate.easeui.model.EaseEvent

@SuppressLint("ViewConstructor")
class EaseChatMultipleSelectMenuView @JvmOverloads constructor(
    private val conversationId: String,
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr), IChatTopExtendMenu {

    private var dismissListener: OnMenuDismissListener? = null
    private val binding: EaseLayoutChatMessagesMultiSelectMenuBinding by lazy {
        EaseLayoutChatMessagesMultiSelectMenuBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    private var listener: OnMultiSelectMenuListener? = null

    private var message: ChatMessage? = null

    init {
        initListener()
    }

    /**
     * Add the message to the selected list.
     * Call it before the view was attached to extend top view.
     */
    fun setSelectedMessage(message: ChatMessage?) {
        this.message = message
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EaseFlowBus.with<EaseEvent>(EaseConstant.EASE_MULTIPLE_SELECT).post(context.mainScope(),
            EaseEvent(EaseEvent.EVENT.ADD.name, EaseEvent.TYPE.NOTIFY, context + conversationId))
        if (EaseIM.getConfig()?.chatConfig?.enableSendCombineMessage == false) {
            binding.ivMultiSelectForward.visibility = INVISIBLE
        } else {
            binding.ivMultiSelectForward.visibility = VISIBLE
        }
        EaseChatMessageMultiSelectHelper.getInstance().init(context, conversationId)
        EaseChatMessageMultiSelectHelper.getInstance().setMultiStyle(context, conversationId, true)
        message?.let {
            EaseChatMessageMultiSelectHelper.getInstance().addChatMessage(context, it)
        }
    }

    private fun initListener() {
        binding.ivMultiSelectDelete.setOnClickListener {
            listener?.onDeleteClick(EaseChatMessageMultiSelectHelper.getInstance().getSortedMessages(context, conversationId) ?: listOf())
        }

        binding.ivMultiSelectForward.setOnClickListener {
            listener?.onForwardClick(EaseChatMessageMultiSelectHelper.getInstance().getSortedMessages(context, conversationId) ?: listOf())
        }
        EaseChatMessageMultiSelectHelper.getInstance().setOnMultipleSelectDataChangeListener(context, conversationId, object :
            OnMultipleSelectChangeListener {

            override fun onMultipleSelectDataChange(key: String) {
                if (key == context + conversationId) {
                    EaseChatMessageMultiSelectHelper.getInstance().isEmpty(context, conversationId)?.let {
                        binding.ivMultiSelectDelete.isEnabled = !it
                        binding.ivMultiSelectForward.isEnabled = !it
                    }
                }
            }

            override fun onMultipleSelectModelChange(key: String, isMultiStyle: Boolean) {
                if (key == context + conversationId && !isMultiStyle) {
                    EaseChatMessageMultiSelectHelper.getInstance().clear(context, conversationId)
                    showTopExtendMenu(false)
                    dismissListener?.onDismiss()
                }
            }


        })
    }

    override fun showTopExtendMenu(isShow: Boolean) {
        visibility = if (isShow) {
            VISIBLE
        } else {
            GONE
        }
    }

    fun setOnMultiSelectMenuListener(listener: OnMultiSelectMenuListener) {
        this.listener = listener
    }

    fun setOnMenuDismissListener(listener: OnMenuDismissListener) {
        this.dismissListener = listener
    }
}