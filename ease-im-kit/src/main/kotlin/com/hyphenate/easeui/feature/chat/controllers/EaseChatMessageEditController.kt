package com.hyphenate.easeui.feature.chat.controllers

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.dialog.EaseAlertDialog
import com.hyphenate.easeui.common.extensions.showSoftKeyboard
import com.hyphenate.easeui.feature.chat.widgets.EaseChatLayout

class EaseChatMessageEditController(
    private val context: Context,
    private val chatLayout: EaseChatLayout
) {

    fun showEditMessageDialog(message: ChatMessage) {
        chatLayout.chatInputMenu?.hideSoftKeyboard()
        chatLayout.chatInputMenu?.showExtendMenu(false)
        val dialog: EaseAlertDialog? = EaseAlertDialog.Builder<EaseAlertDialog>(context)
            .setGravity(Gravity.BOTTOM)
            .setContentView(R.layout.ease_chat_message_edit_dialog)
            .setFullWidth()
            .setCancelable(true)
            .show()
        val editText: EditText? = dialog?.getViewById(R.id.edt_msg_edit)
        val tvDone: ImageButton? = dialog?.getViewById(R.id.ib_done)
        val content: String = (message.body as ChatTextMessageBody).message
        dialog?.setOnClickListener(R.id.ib_done, View.OnClickListener {
            if (dialog != null) {
                dialog.dismiss()
            }
            val newContent = editText?.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(newContent)) {
                val textMessageBody = ChatTextMessageBody(newContent)
                textMessageBody.targetLanguages = (message.body as ChatTextMessageBody).targetLanguages
                chatLayout.modifyMessage(message.msgId, textMessageBody)
            }
        })
        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) {
                    if (editText != null) {
                        editText.hint = content
                    }
                    if (tvDone != null) {
                        tvDone.isEnabled = false
                    }
                } else {
                    editText?.setSelection(s.length)
                    if (tvDone != null) {
                        tvDone.isEnabled = true
                    }
                    if (s.toString() == content) {
                        if (tvDone != null) {
                            tvDone.isEnabled = false
                        }
                    }
                }
            }
        })
        editText?.setText(content)
        editText?.let {
            it.setHorizontallyScrolling(false)
            var maxLines = context.resources.getInteger(R.integer.ease_input_edit_text_max_lines)
            maxLines = if (maxLines <= 0) 4 else maxLines
            it.maxLines = maxLines
        }
        editText?.showSoftKeyboard()
    }
}