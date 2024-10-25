package io.agora.uikit.feature.chat.forward.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.bus.EaseFlowBus
import io.agora.uikit.common.extensions.mainScope
import io.agora.uikit.common.extensions.plus
import io.agora.uikit.databinding.EaseLayoutChatMessagesMultiSelectMenuBinding
import io.agora.uikit.feature.chat.forward.helper.EaseChatMessageMultiSelectHelper
import io.agora.uikit.feature.chat.interfaces.IChatTopExtendMenu
import io.agora.uikit.feature.chat.interfaces.OnMultipleSelectChangeListener
import io.agora.uikit.interfaces.OnMenuDismissListener
import io.agora.uikit.interfaces.OnMultiSelectMenuListener
import io.agora.uikit.model.EaseEvent

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