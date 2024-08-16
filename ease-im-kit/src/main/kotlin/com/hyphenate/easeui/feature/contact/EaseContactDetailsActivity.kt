package com.hyphenate.easeui.feature.contact

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.EaseChatActivity
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatSilentModeResult
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.configs.EaseBottomMenuConfig
import com.hyphenate.easeui.configs.EaseDetailMenuConfig
import com.hyphenate.easeui.common.dialog.SimpleListSheetDialog
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.configs.setStatusStyle
import com.hyphenate.easeui.databinding.EaseLayoutContactDetailsBinding
import com.hyphenate.easeui.feature.contact.adapter.EaseContactDetailItemAdapter
import com.hyphenate.easeui.feature.contact.interfaces.IEaseContactResultView
import com.hyphenate.easeui.feature.chat.enums.EaseChatType
import com.hyphenate.easeui.feature.search.EaseSearchActivity
import com.hyphenate.easeui.feature.search.EaseSearchType
import com.hyphenate.easeui.interfaces.EaseContactListener
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.interfaces.SimpleListSheetItemClickListener
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.model.EaseMenuItem
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.model.isCurrentUser
import com.hyphenate.easeui.viewmodel.contacts.EaseContactListViewModel
import com.hyphenate.easeui.viewmodel.contacts.IContactListRequest

open class EaseContactDetailsActivity:EaseBaseActivity<EaseLayoutContactDetailsBinding>(),
     View.OnClickListener, IEaseContactResultView, OnMenuItemClickListener{
    protected var user: EaseUser? = null
    private var dialog: SimpleListSheetDialog? = null
    private var gridAdapter: EaseContactDetailItemAdapter? = null
    private var contactViewModel: IContactListRequest? = null
    /**
     * The clipboard manager.
     */
    private val clipboard by lazy { mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }


    private val contactListener = object : EaseContactListener() {
        override fun onContactDeleted(username: String?) {
            if (username == user?.userId){
                finish()
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): EaseLayoutContactDetailsBinding{
        return EaseLayoutContactDetailsBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(KEY_USER_INFO,EaseUser::class.java)
        }else{
            intent.getSerializableExtra(KEY_USER_INFO) as EaseUser?
        }
        initView()
        initData()
        initListener()
        initEvent()
    }

    open fun initView(){
        initSwitch()
        initUserAvatarInfo()
        initMenu()
    }

    open fun initData(){
        contactViewModel = ViewModelProvider(this)[EaseContactListViewModel::class.java]
        contactViewModel?.attachView(this)

        contactViewModel?.let {
            val isLoad = EaseIM.isLoadBlockListFromServer?: false
            if (!isLoad){
                it.fetchBlockListFromServer()
            }else{
                it.getBlockListFromLocal()
            }
        }
    }

    private fun initSwitch(){
        val isSilent = EaseIM.getCache().getMutedConversationList().containsKey(user?.userId)
        if (isSilent){
            binding.switchItemDisturb.setChecked(true)
            binding.icNotice.visibility = View.VISIBLE
        }else{
            binding.switchItemDisturb.setChecked(false)
            binding.icNotice.visibility = View.GONE
        }
        binding.switchItemDisturb.setSwitchTarckDrawable(R.drawable.ease_switch_track_selector)
        binding.switchItemDisturb.setSwitchThumbDrawable(R.drawable.ease_switch_thumb_selector)

        binding.switchItemBlack.setSwitchTarckDrawable(R.drawable.ease_switch_track_selector)
        binding.switchItemBlack.setSwitchThumbDrawable(R.drawable.ease_switch_thumb_selector)
    }

    open fun initUserAvatarInfo(){
        EaseIM.getConfig()?.avatarConfig?.setStatusStyle(binding.epPresence.getStatusView(),4.dpToPx(mContext),
            ContextCompat.getColor(mContext, R.color.ease_color_background))
        binding.epPresence.setPresenceStatusMargin(end = -3, bottom = -3)
        binding.epPresence.setPresenceStatusSize(resources.getDimensionPixelSize(R.dimen.ease_contact_status_icon_size))

        val layoutParams = binding.epPresence.getUserAvatar().layoutParams
        layoutParams.width = 100.dpToPx(this)
        layoutParams.height = 100.dpToPx(this)
        binding.epPresence.getUserAvatar().layoutParams = layoutParams

        user?.let {
            EaseIM.getUserProvider()?.getUser(it.userId)?.let { profile->
                binding.epPresence.setUserAvatarData(profile)
                binding.tvName.text = profile.getRemarkOrName()
                binding.tvNumber.text = profile.id
            }

            if (it.isCurrentUser()) {
                binding.gvGridview.visibility = View.GONE
                binding.titleBar.getToolBar().menu.forEach {menu-> menu.isVisible = false }
                binding.switchItemDisturb.visibility = View.GONE
                binding.switchItemBlack.visibility = View.GONE
            }
        }
    }

    private fun initMenu(){
        val menuData = getDetailItem()?.filter { it.isVisible }?.toMutableList()
        menuData?.let { EaseDetailMenuConfig(mContext).sortByOrder(it) }
        menuData?.let { menu->
            binding.gvGridview.let {
                it.horizontalSpacing = 8.dpToPx(this)
                it.numColumns = menu.size
                gridAdapter = EaseContactDetailItemAdapter(this, 1, menu)
                it.adapter = gridAdapter
            }
        }
        if (menuData.isNullOrEmpty()) binding.functionLayout.visibility = View.GONE

    }

    open fun getDetailItem():MutableList<EaseMenuItem>?{
        return EaseDetailMenuConfig(this).getDefaultContactDetailMenu()
    }

    open fun initListener(){
        EaseIM.addContactListener(contactListener)
        binding.tvNumber.setOnClickListener(this)
        binding.switchItemDisturb.setOnClickListener(this)
        binding.switchItemBlack.setOnClickListener(this)
        binding.itemClear.setOnClickListener(this)
        gridAdapter?.setContactDetailItemClickListener(this)
        binding.titleBar.setNavigationOnClickListener { mContext.onBackPressed() }
        binding.titleBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_more -> {
                    showDeleteContentDialog()
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EaseIM.removeContactListener(contactListener)
    }

    open fun initEvent() {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE.name).register(this) {
            if (it.isContactChange && it.message == user?.userId && !mContext.isFinishing) {
                finish()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.item_clear -> {
                showClearMsgDialog()
            }
            R.id.tv_number -> {
                copyId()
            }
            R.id.switch_item_disturb -> {
                binding.switchItemDisturb.switch?.let { switch ->
                    val isChecked = switch.isChecked.not()
                    user?.let {
                        if (isChecked){
                            contactViewModel?.makeSilentModeForConversation(it.userId,ChatConversationType.Chat)
                        }else{
                            contactViewModel?.cancelSilentForConversation(it.userId,ChatConversationType.Chat)
                        }
                    }
                }
            }
            R.id.switch_item_black -> {
                binding.switchItemBlack.switch?.let { switch ->
                    val isChecked = switch.isChecked.not()
                    if (isChecked){
                        showBlockDialog()
                    }else{
                        user?.let {
                            contactViewModel?.removeUserFromBlockList(it.userId)
                        }
                    }
                }
            }
            else -> {}
        }
    }

    open fun copyId(){
        val id = binding.tvNumber.text
        if (id.isNotEmpty()){
            clipboard.setPrimaryClip(
                ClipData.newPlainText(null, id)
            )
        }
    }

    private fun showClearMsgDialog(){
        val clearDialog = CustomDialog(
            context = this@EaseContactDetailsActivity,
            title = resources.getString(R.string.ease_dialog_clear),
            isEditTextMode = false,
            onLeftButtonClickListener = {

            },
            onRightButtonClickListener = {
                contactViewModel?.clearConversationMessage(user?.userId)
            }
        )
        clearDialog.show()
    }

    private fun showBlockDialog(){
        val blackDialog = CustomDialog(
            this@EaseContactDetailsActivity,
            resources.getString(R.string.ease_dialog_black_title),
            resources.getString(R.string.ease_dialog_black),
            false,
            onLeftButtonClickListener = {},
            onRightButtonClickListener = {
                user?.let {
                    contactViewModel?.addUserToBlockList(mutableListOf(it.userId))
                }
            }
        )
        blackDialog.show()
    }

    open fun updateBlockSwitch(list:MutableList<EaseUser>){
        list.map {
            if (it.userId == user?.userId){
                binding.switchItemBlack.setChecked(true)
                updateBlockLayout(true)
            }else{
                binding.switchItemBlack.setChecked(false)
                updateBlockLayout(false)
            }
        }
    }

    open fun updateBlockLayout(isChecked:Boolean){
        if (isChecked){
            binding.run {
                functionLayout.visibility = View.GONE
                switchItemDisturb.visibility = View.GONE
                itemClear.visibility = View.GONE
            }
        }else{
            binding.run {
                functionLayout.visibility = View.VISIBLE
                switchItemDisturb.visibility = View.VISIBLE
                itemClear.visibility = View.VISIBLE
            }
        }
    }

    open fun getDeleteDialogMenu(): MutableList<EaseMenuItem>?{
        return EaseBottomMenuConfig(this).getDefaultContactBottomSheetMenu()
    }

    open fun simpleSheetMenuItemClick(position: Int, menu: EaseMenuItem){
        when(menu.menuId){
            R.id.bottom_sheet_item_remove_contact -> {
                val dialogDelete = CustomDialog(
                    context = this@EaseContactDetailsActivity,
                    title = resources.getString(R.string.ease_dialog_delete_contact_title),
                    subtitle = resources.getString(R.string.ease_dialog_delete_contact_subtitle,user?.getNickname()),
                    isEditTextMode = false,
                    onLeftButtonClickListener = {
                        dialog?.dismiss()
                    },
                    onRightButtonClickListener = {
                        user?.userId?.let { contactViewModel?.deleteContact(it,false) }
                        dialog?.dismiss()
                    }
                )
                dialogDelete.show()
            }
            else -> {}
        }
    }

    private fun showDeleteContentDialog(){
        val context = this@EaseContactDetailsActivity
        val menu = getDeleteDialogMenu()
        dialog = SimpleListSheetDialog(
            context = context,
            itemList = menu,
            itemListener = object : SimpleListSheetItemClickListener{
                override fun onItemClickListener(position: Int, menu: EaseMenuItem) {
                    simpleSheetMenuItemClick(position, menu)
                }
            })
        supportFragmentManager.let { dialog?.show(it,"more_dialog") }
    }

    override fun fetchBlockListFromServerSuccess(list: MutableList<EaseUser>) {
        EaseIM.isLoadBlockListFromServer = true
        updateBlockSwitch(list)
    }

    override fun fetchBlockListFromServerFail(code: Int, error: String) {
        EaseIM.isLoadBlockListFromServer = true
    }

    override fun getBlockListFromLocalSuccess(list: MutableList<EaseUser>) {
        updateBlockSwitch(list)
    }

    override fun addUserToBlockListSuccess() {
        binding.switchItemBlack.setChecked(true)
        updateBlockLayout(true)
    }

    override fun addUserToBlockListFail(code: Int, error: String) {
        binding.switchItemBlack.setChecked(false)
        updateBlockLayout(false)
    }

    override fun removeUserFromBlockListSuccess() {
        binding.switchItemBlack.setChecked(false)
        updateBlockLayout(false)
    }

    override fun removeUserFromBlockListFail(code: Int, error: String) {
        ChatLog.e(TAG,"removeUserFromBlockListFail $code $error")
    }

    override fun clearConversationSuccess(conversationId: String?) {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE.name)
            .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.REMOVE.name, EaseEvent.TYPE.CONVERSATION, conversationId))
    }

    override fun clearConversationFail(code: Int, error: String?) {
        ChatLog.e(TAG,"deleteConversationFail $code $error")
    }

    override fun deleteContactSuccess() {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE.name)
            .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.REMOVE.name, EaseEvent.TYPE.CONTACT, user?.userId))
        finish()
    }

    override fun deleteContactFail(code: Int, error: String) {
        ChatLog.e(TAG,"deleteContactFail $code $error")
    }

    override fun makeSilentForContactSuccess(silentResult: ChatSilentModeResult) {
        binding.icNotice.visibility = View.VISIBLE
        binding.switchItemDisturb.setChecked(true)
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.UPDATE.name)
            .post(lifecycleScope
                , EaseEvent(EaseEvent.EVENT.UPDATE.name
                    , EaseEvent.TYPE.SILENT, user?.userId))

    }

    override fun makeSilentForContactFail(
        code: Int,
        error: String?
    ) {
        ChatLog.e(TAG,"makeSilentForConversationFail $code $error")
    }

    override fun cancelSilentForContactSuccess() {
        binding.icNotice.visibility = View.GONE
        binding.switchItemDisturb.setChecked(false)
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.UPDATE.name)
            .post(mContext.mainScope()
                , EaseEvent(EaseEvent.EVENT.UPDATE.name
                    , EaseEvent.TYPE.CONVERSATION, user?.userId))
    }

    override fun cancelSilentForContactFail(code: Int, error: String?) {
        ChatLog.e(TAG,"cancelSilentForContactFail $code $error")
    }

    override fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean {
        item?.let {
            when(item.menuId){
                R.id.extend_item_message -> {
                    user?.userId?.let { conversationId ->
                        EaseChatActivity.actionStart(mContext,
                            conversationId, EaseChatType.SINGLE_CHAT)
                    }
                }
                R.id.extend_item_search -> {
                    mContext.startActivity(EaseSearchActivity.createIntent(mContext, EaseSearchType.MESSAGE,user?.userId))
                }
                else -> {}
            }
            return true
        }
        return false
    }

    companion object {
        private const val TAG = "EaseContactDetailsActivity"
        private const val KEY_USER_INFO = "userInfo"

        fun createIntent(
            context: Context,
            user: EaseUser
        ): Intent {
            val intent = Intent(context, EaseContactDetailsActivity::class.java)
            intent.putExtra(KEY_USER_INFO, user)
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return it
                }
            }
            return intent
        }
    }

}