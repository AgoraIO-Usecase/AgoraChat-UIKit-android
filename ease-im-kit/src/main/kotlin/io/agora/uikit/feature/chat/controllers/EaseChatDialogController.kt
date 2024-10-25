package io.agora.uikit.feature.chat.controllers

import android.content.Context
import io.agora.uikit.R
import io.agora.uikit.common.dialog.CustomDialog
import io.agora.uikit.common.impl.OnSuccess
import io.agora.uikit.feature.chat.widgets.EaseChatLayout

class EaseChatDialogController(
    private val context: Context,
    private val chatLayout: EaseChatLayout
) {

    fun showDeleteDialog(context: Context, isSuccessMessage: Boolean, onSuccess: OnSuccess) {
        val clearDialog = CustomDialog(
            context = context,
            title = context.resources.getString(R.string.ease_chat_dialog_delete_title),
            subtitle = if (isSuccessMessage) context.resources.getString(R.string.ease_chat_dialog_delete_content) else "",
            isEditTextMode = false,
            onLeftButtonClickListener = {

            },
            onRightButtonClickListener = {
               onSuccess.invoke()
            }
        )
        clearDialog.show()
    }

    fun showRecallDialog(context: Context, onSuccess: OnSuccess) {
        val clearDialog = CustomDialog(
            context = context,
            title = context.resources.getString(R.string.ease_chat_dialog_recall_title),
            isEditTextMode = false,
            onLeftButtonClickListener = {

            },
            onRightButtonClickListener = {
               onSuccess.invoke()
            }
        )
        clearDialog.show()
    }

    fun showResendDialog(context: Context, onSuccess: OnSuccess) {
        val resendDialog = CustomDialog(
            context = context,
            title = context.resources.getString(R.string.ease_chat_dialog_resend_title),
            isEditTextMode = false,
            onLeftButtonClickListener = {

            },
            onRightButtonClickListener = {
                onSuccess.invoke()
            }
        )
        resendDialog.show()
    }
}