package io.agora.uikit.feature.chat.controllers

import android.content.Context
import androidx.fragment.app.FragmentActivity
import io.agora.uikit.R
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.feature.chat.widgets.EaseChatLayout
import io.agora.uikit.feature.chat.report.EaseReportSheetDialog

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