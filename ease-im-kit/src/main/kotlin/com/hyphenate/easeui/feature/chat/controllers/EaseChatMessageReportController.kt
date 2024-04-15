package com.hyphenate.easeui.feature.chat.controllers

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.widgets.EaseChatLayout
import com.hyphenate.easeui.feature.chat.report.EaseReportSheetDialog

class EaseChatMessageReportController(
    private val context: Context,
    private val chatLayout: EaseChatLayout,
    private val reportTag:Array<String>? = context.resources.getStringArray(R.array.report_reason),
) {
    fun showReportDialog(message: ChatMessage?){
        val reportTagList = reportTag?.toMutableList()
        val report = EaseReportSheetDialog(
            context = context,
            itemList = reportTagList?: context.resources.getStringArray(R.array.report_reason).toMutableList(),
            onRightButtonClickListener = { position, reason ->
                val tag = context.resources.getStringArray(R.array.report_tag).let {
                    it[position]
                } ?: "unknown"
                reason?.let { chatLayout.reportMessage(tag, reason, message) }
            },
            onLeftButtonClickListener = {}
        )
        (context as FragmentActivity).supportFragmentManager.let {  report.show(it,"chat_report") }
    }
}