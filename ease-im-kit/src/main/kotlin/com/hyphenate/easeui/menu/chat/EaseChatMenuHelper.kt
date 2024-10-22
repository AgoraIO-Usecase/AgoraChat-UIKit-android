package com.hyphenate.easeui.menu.chat

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageDirection
import com.hyphenate.easeui.common.ChatMessageStatus
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.canEdit
import com.hyphenate.easeui.feature.chat.reaction.EaseMessageMenuReactionView
import com.hyphenate.easeui.interfaces.OnMenuChangeListener
import com.hyphenate.easeui.interfaces.OnMenuDismissListener
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.menu.EaseMenuHelper
import com.hyphenate.easeui.menu.EaseMenuItemView
import com.hyphenate.easeui.model.EaseMenuItem

class EaseChatMenuHelper: EaseMenuHelper() {
    private var message: ChatMessage? = null
    private var onMenuChangeListener: OnMenuChangeListener? = null
    fun initMenuWithMessage(message: ChatMessage?) {
        this.message = message
        setMenuOrientation(EaseMenuItemView.MenuOrientation.HORIZONTAL)
        setMenuGravity(EaseMenuItemView.MenuGravity.LEFT)
        showCancel(false)
        setDefaultMenus()
        setMenuVisibleByMessageType()
        showReactionView()
        onMenuChangeListener?.onPreMenu(this, message)
        setOnMenuDismissListener(object : OnMenuDismissListener {
            override fun onDismiss() {
                onMenuChangeListener?.onDismiss()
            }
        })
    }

    private fun showReactionView() {
        if (EaseIM.getConfig()?.chatConfig?.enableMessageReaction == true && message?.status() == ChatMessageStatus.SUCCESS){
            view?.let { view->
                message?.run {
                    val enableWxStyle = EaseIM.getConfig()?.chatConfig?.enableWxMessageStyle
                    EaseMessageMenuReactionView(
                        view.context,
                        spanCount = if (enableWxStyle == true) 6 else 7
                    ).let {
                        it.setupWithMessage(this)
                        it.bindWithMenuHelper(this@EaseChatMenuHelper)
                        it.showReaction()
                        it.setMoreReactionClickListener{
                            dismiss()
                        }
                        addTopView(it)
                    }
                }
            }
        }
    }

    fun hindReactionView(){
        view?.let { view->
            clearTopView()
        }
    }

    private fun setMenuVisibleByMessageType() {
        message?.let {
            val type: ChatMessageType = it.type
            setAllItemsVisible(false)
            findItemVisible(R.id.action_chat_delete, true)
            findItem(R.id.action_chat_delete)
                ?.title = getContext()?.getString(R.string.ease_action_delete)
            if (it.status() == ChatMessageStatus.SUCCESS && it.direct() === ChatMessageDirection.SEND) {
                findItemVisible(R.id.action_chat_recall, canRecallMessage(it))
            }
            if (it.status() == ChatMessageStatus.SUCCESS)
                findItemVisible(R.id.action_chat_report, true)
            if (type == ChatMessageType.TXT) findItemVisible(R.id.action_chat_copy, true)
            if (it.chatType === ChatType.GroupChat && !it.isChatThreadMessage && it.chatThread == null) {
                findItemVisible(R.id.action_chat_thread, true)
            }else{
                findItemVisible(R.id.action_chat_thread, false)
            }

            if (it.direct() === ChatMessageDirection.RECEIVE) {
                findItemVisible(R.id.action_chat_recall, false)
            }
            if (it.status() !== ChatMessageStatus.SUCCESS) {
                findItemVisible(R.id.action_chat_recall, false)
                findItemVisible(R.id.action_chat_thread, false)
            }
            findItemVisible(
                R.id.action_chat_reply,
                it.status() === ChatMessageStatus.SUCCESS
                        && EaseIM.getConfig()?.chatConfig?.enableReplyMessage == true
            )
            findItemVisible(
                R.id.action_chat_select,
                it.status() === ChatMessageStatus.SUCCESS
                        && EaseIM.getConfig()?.chatConfig?.enableSendCombineMessage == true
            )
            findItemVisible(R.id.action_chat_edit, it.canEdit())
            if (type == ChatMessageType.TXT && it.status() === ChatMessageStatus.SUCCESS){
                findItemVisible(R.id.action_chat_translation,EaseIM.getConfig()?.chatConfig?.enableTranslationMessage == true)
            }
            findItemVisible(R.id.action_chat_forward, it.status() === ChatMessageStatus.SUCCESS)
            findItemVisible(R.id.action_chat_multi_select, it.status() === ChatMessageStatus.SUCCESS)
            if (it.isChatThreadMessage){
                findItemVisible(R.id.action_chat_delete, false)
                findItemVisible(R.id.action_chat_recall, false)
            }
            if (message?.isChatThreadMessage == false){
                findItemVisible(R.id.action_chat_pin_message,true)
            }
        }

    }

    private fun canRecallMessage(message: ChatMessage): Boolean {
        EaseIM.getConfig()?.chatConfig?.timePeriodCanRecallMessage?.let {
            if (it != -1L && it > 0) return System.currentTimeMillis() - message.localTime() <= it
        }
        return true
    }

    private fun setDefaultMenus() {
        clear()
        MENU_ITEM_IDS.forEachIndexed { index, item ->
            getContext()?.let {
                if (item == R.id.action_chat_translation){
                    if (isShowTranslation()){
                        addItemMenu(item, (index + 1) * 10, it.getString(R.string.ease_action_hide_translation), resourceId = MENU_ICONS[index])
                    }else{
                        addItemMenu(item, (index + 1) * 10, it.getString(MENU_TITLES[index]), resourceId = MENU_ICONS[index])
                    }
                }else{
                    addItemMenu(item, (index + 1) * 10, it.getString(MENU_TITLES[index]), resourceId = MENU_ICONS[index])
                }
            }
        }
        getContext()?.run {
            // add forward menu
            addItemMenu(R.id.action_chat_forward, (getTargetMenuIndex(R.id.action_chat_copy) + 1 ) * 10 + 5
                , getString(R.string.ease_action_forward), resourceId = R.drawable.ease_chat_item_menu_forward)
            // add multi select menu
            if (EaseIM.getConfig()?.chatConfig?.enableSendCombineMessage == true) {
                addItemMenu(R.id.action_chat_multi_select, (getTargetMenuIndex(R.id.action_chat_edit) + 1 ) * 10 + 5
                    , getString(R.string.ease_action_multi_select), resourceId = R.drawable.ease_chat_item_menu_multi)
            }
            if (EaseIM.getConfig()?.chatConfig?.enableChatThreadMessage == true) {
                addItemMenu(R.id.action_chat_thread,(getTargetMenuIndex(R.id.action_chat_copy) + 1 ) * 10 + 7
                    ,getString(R.string.ease_action_thread),resourceId = R.drawable.ease_chat_item_menu_topic)
            }
            if (EaseIM.getConfig()?.chatConfig?.enableChatPingMessage == true){
                addItemMenu(R.id.action_chat_pin_message,(getTargetMenuIndex(R.id.action_chat_edit) + 1 ) * 10 + 7
                    ,getString(R.string.ease_action_pin), resourceId = R.drawable.ease_icon_chat_pininfo_light)
            }
        }
    }

    private fun getTargetMenuIndex(id: Int): Int {
        MENU_ITEM_IDS.forEachIndexed { index, item ->
            if (item == id) return index
        }
        return MENU_ITEM_IDS.size
    }

    private fun isShowTranslation():Boolean{
        val containsKey = message?.ext()?.containsKey(EaseConstant.TRANSLATION_STATUS)
        message?.let {
            if (it.body is ChatTextMessageBody){
                val body = it.body as ChatTextMessageBody
                val translations = body.translations
                containsKey?.let { hasKey->
                    if (hasKey){
                        val isTranslation = it.getBooleanAttribute(EaseConstant.TRANSLATION_STATUS)
                        isTranslation.let {
                            return it
                        }
                    }
                }
                return translations.size > 0
            }
        }
        return false
    }


    fun setOnMenuChangeListener(listener: OnMenuChangeListener?) {
        this.onMenuChangeListener = listener
    }

    override fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        super.setOnMenuItemClickListener(object : OnMenuItemClickListener {
            override fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean {
                if (onMenuChangeListener?.onMenuItemClick(item, message) == true) {
                    return true
                }
                return listener?.onMenuItemClick(item, position) == true
            }
        })

    }

    override fun release(){
        message = null
       super.release()
    }

    companion object {
        val MENU_ITEM_IDS = intArrayOf(
            R.id.action_chat_copy,
            R.id.action_chat_reply,
            R.id.action_chat_recall,
            R.id.action_chat_edit,
            R.id.action_chat_translation,
            R.id.action_chat_report,
            R.id.action_chat_delete,
        )
        val MENU_TITLES = intArrayOf(
            R.string.ease_action_copy,
            R.string.ease_action_reply,
            R.string.ease_action_recall,
            R.string.ease_action_edit,
            R.string.ease_action_translation,
            R.string.ease_action_report,
            R.string.ease_action_delete,

        )
        val MENU_ICONS = intArrayOf(
            R.drawable.ease_chat_item_menu_copy,
            R.drawable.ease_chat_item_menu_reply,
            R.drawable.ease_chat_item_menu_unsent,
            R.drawable.ease_chat_item_menu_edit,
            R.drawable.ease_chat_item_menu_translation,
            R.drawable.ease_chat_item_menu_report,
            R.drawable.ease_chat_item_menu_delete,

        )
    }

}