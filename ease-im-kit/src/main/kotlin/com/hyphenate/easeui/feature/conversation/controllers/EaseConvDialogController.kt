package com.hyphenate.easeui.feature.conversation.controllers

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.dialog.EaseNewChatBottomSheetFragment
import com.hyphenate.easeui.common.dialog.SimpleListSheetDialog
import com.hyphenate.easeui.common.dialog.SimpleSheetType
import com.hyphenate.easeui.feature.group.EaseCreateGroupActivity
import com.hyphenate.easeui.interfaces.SimpleListSheetItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem

class EaseConvDialogController(
    private val context: Context,
    private val fragment: Fragment
) {

    fun showMoreDialog(addContactAction: (String) -> Unit){
        val context = (context as FragmentActivity)
        val mutableListOf = mutableListOf(
            EaseMenuItem(
                menuId = R.id.ease_action_new_conversation,
                title = context.getString(R.string.ease_conv_action_new_conversation),
                resourceId = R.drawable.ease_conv_new_chat,
                titleColor = ContextCompat.getColor(context, R.color.ease_color_primary)
            ),
            EaseMenuItem(
                menuId = R.id.ease_action_add_contact,
                title = context.getString(R.string.ease_conv_action_add_contact),
                resourceId =  R.drawable.ease_conv_add_contact,
                titleColor = ContextCompat.getColor(context, R.color.ease_color_primary)
            ),
            EaseMenuItem(
                menuId = R.id.ease_action_create_group,
                title = context.getString(R.string.ease_conv_action_create_group),
                resourceId =  R.drawable.ease_conv_new_group,
                titleColor = ContextCompat.getColor(context, R.color.ease_color_primary)
            ),
        )
        val dialog = SimpleListSheetDialog(
            context = context,
            itemList = mutableListOf,
            type = SimpleSheetType.ITEM_LAYOUT_DIRECTION_START)
        dialog.setSimpleListSheetItemClickListener(object : SimpleListSheetItemClickListener {
            override fun onItemClickListener(position: Int, menu: EaseMenuItem) {
                dialog.dismiss()
                when(menu.menuId){
                    R.id.ease_action_new_conversation -> {
                        val fragment = EaseNewChatBottomSheetFragment()
                        fragment.show(this@EaseConvDialogController.fragment.childFragmentManager, "createNewChat")
                    }
                    R.id.ease_action_add_contact -> {
                        showAddContactDialog(addContactAction)
                    }
                    R.id.ease_action_create_group -> {
                        EaseCreateGroupActivity.actionStart(context)
                    }
                    else -> {}
                }
            }
        })
        context.supportFragmentManager.let { dialog?.show(it,"ease_conversation_action_more") }
    }

    fun showAddContactDialog(addContactAction: (String) -> Unit){
        val context = (context as FragmentActivity)
        val contactDialog = CustomDialog(
            context = context,
            title = context.getString(R.string.ease_conv_action_add_contact),
            subtitle = context.getString(R.string.ease_conv_dialog_add_contact),
            rightButtonText = context.getString(R.string.ease_dialog_right_text),
            isEditTextMode = true,
            onInputModeConfirmListener = {
                addContactAction.invoke(it)
            }
        )
        contactDialog.show()
    }
}