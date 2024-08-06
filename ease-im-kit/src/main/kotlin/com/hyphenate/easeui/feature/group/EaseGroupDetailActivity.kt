package com.hyphenate.easeui.feature.group

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.EaseChatActivity
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatSilentModeResult
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.enums.EaseGroupMemberType
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.extensions.isOwner
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.configs.EaseBottomMenuConfig
import com.hyphenate.easeui.configs.EaseDetailMenuConfig
import com.hyphenate.easeui.configs.setAvatarStyle
import com.hyphenate.easeui.databinding.EaseLayoutGroupDetailsBinding
import com.hyphenate.easeui.common.dialog.SimpleListSheetDialog
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.feature.contact.adapter.EaseContactDetailItemAdapter
import com.hyphenate.easeui.feature.group.interfaces.IEaseGroupResultView
import com.hyphenate.easeui.feature.chat.enums.EaseChatType
import com.hyphenate.easeui.feature.search.EaseSearchActivity
import com.hyphenate.easeui.feature.search.EaseSearchType
import com.hyphenate.easeui.interfaces.EaseGroupListener
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.interfaces.SimpleListSheetItemClickListener
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.model.EaseMenuItem
import com.hyphenate.easeui.provider.getSyncProfile
import com.hyphenate.easeui.viewmodel.group.EaseGroupViewModel
import com.hyphenate.easeui.viewmodel.group.IGroupRequest
import com.hyphenate.easeui.widget.EaseSwitchItemView
import kotlinx.coroutines.launch

open class EaseGroupDetailActivity:EaseBaseActivity<EaseLayoutGroupDetailsBinding>(),
    EaseSwitchItemView.OnCheckedChangeListener, View.OnClickListener,
    IEaseGroupResultView, OnMenuItemClickListener {
    private var group: ChatGroup? = null
    protected var groupId: String? = null
    private var dialog: SimpleListSheetDialog? = null
    private var gridAdapter: EaseContactDetailItemAdapter? = null
    private var groupViewModel: IGroupRequest? = null

    companion object {
        private const val TAG = "EaseGroupDetailActivity"
        private const val KEY_GROUP_INFO = "groupId"

        fun createIntent(
            context: Context,
            groupId: String
        ): Intent {
            val intent = Intent(context, EaseGroupDetailActivity::class.java)
            intent.putExtra(KEY_GROUP_INFO, groupId)
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return it
                }
            }
            return intent
        }
    }

    private val groupChangeListener = object : EaseGroupListener() {

        override fun onGroupDestroyed(groupId: String?, groupName: String?) {
            if (this@EaseGroupDetailActivity.groupId == groupId ){
                finish()
            }
        }

        override fun onSpecificationChanged(group: ChatGroup?) {
            group?.let {
                if (groupId == it.groupId){
                    binding.tvSignature.text = it.description
                    binding.tvName.text = it.groupName
                }
            }
        }

        override fun onAnnouncementChanged(groupId: String?, announcement: String?) {
            if (this@EaseGroupDetailActivity.groupId == groupId){
                binding.tvSignature.text = announcement
            }
        }

        override fun onOwnerChanged(groupId: String?, newOwner: String?, oldOwner: String?) {
            if (this@EaseGroupDetailActivity.groupId == groupId && newOwner == EaseIM.getCurrentUser()?.id){
                mainScope().launch {
                    binding.itemSpacing.visibility = View.VISIBLE
                    binding.itemGroupName.visibility = View.VISIBLE
                    binding.itemGroupDescribe.visibility = View.VISIBLE
                }
            }
        }

        override fun onUserRemoved(groupId: String?, groupName: String?) {
            super.onUserRemoved(groupId, groupName)
            if (groupId == this@EaseGroupDetailActivity.groupId){
                EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.LEAVE.name)
                    .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.LEAVE.name, EaseEvent.TYPE.GROUP, groupId))
                finish()
            }
        }

    }

    /**
     * The clipboard manager.
     */
    private val clipboard by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun getViewBinding(inflater: LayoutInflater): EaseLayoutGroupDetailsBinding{
        return EaseLayoutGroupDetailsBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupId = intent.getStringExtra(KEY_GROUP_INFO)
        groupId?.let {
            group = ChatClient.getInstance().groupManager().getGroup(it)
        }

        initView()
        initListener()
        initData()
        initEvent()
    }

    protected open fun initView(){
        EaseIM.getConfig()?.avatarConfig?.setAvatarStyle(binding.ivGroupAvatar)
        initSwitch()
        updateView()
        initMenu()
    }

    private fun initSwitch(){
        val isSilent = EaseIM.getCache().getMutedConversationList().containsKey(groupId)
        if (isSilent){
            binding.switchItemDisturb.setChecked(true)
        }else{
            binding.switchItemDisturb.setChecked(false)
        }
        binding.switchItemDisturb.setSwitchTarckDrawable(R.drawable.ease_switch_track_selector)
        binding.switchItemDisturb.setSwitchThumbDrawable(R.drawable.ease_switch_thumb_selector)
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
        return EaseDetailMenuConfig(this).getDefaultGroupDetailMenu()
    }

    protected open fun initListener(){
        EaseIM.addGroupChangeListener(groupChangeListener)
        binding.titleBar.setNavigationOnClickListener {
            mContext.onBackPressed()
        }
        binding.titleBar.setOnMenuItemClickListener { item->
            when(item.itemId){
                R.id.action_more -> {
                    showDialog()
                }
            }
            true
        }
        binding.itemMemberList.setOnClickListener(this)
        binding.switchItemDisturb.setOnCheckedChangeListener(this)
        binding.itemClear.setOnClickListener(this)
        binding.itemGroupName.setOnClickListener(this)
        binding.itemGroupDescribe.setOnClickListener(this)
        binding.tvNumber.setOnClickListener(this)
        gridAdapter?.setContactDetailItemClickListener(this)
    }

    protected open fun initEvent() {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.LEAVE.name).register(this) {
            if (it.isGroupChange && it.message == groupId && !mContext.isFinishing) {
                finish()
            }
        }
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.DESTROY.name).register(this) {
            if (it.isGroupChange && it.message == groupId && !mContext.isFinishing) {
                finish()
            }
        }
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.UPDATE + EaseEvent.TYPE.GROUP).register(this) {
            if (it.isGroupChange && it.message == groupId) {
                group = ChatClient.getInstance().groupManager().getGroup(groupId)
                if (it.event == EaseConstant.EVENT_UPDATE_GROUP_OWNER){
                    binding.itemSpacing.visibility = View.GONE
                    binding.itemGroupName.visibility = View.GONE
                    binding.itemGroupDescribe.visibility = View.GONE
                }else{
                    updateView()
                }
            }
        }
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE + EaseEvent.TYPE.GROUP + EaseEvent.TYPE.CONTACT).register(this) {
            if (it.isGroupChange && it.message == groupId) {
                group = ChatClient.getInstance().groupManager().getGroup(groupId)
                binding.itemMemberList.tvContent?.text = group?.memberCount?.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EaseIM.removeGroupChangeListener(groupChangeListener)
    }

    protected open fun initData(){
        groupViewModel = ViewModelProvider(this)[EaseGroupViewModel::class.java]
        groupViewModel?.attachView(this)

        fetchGroupDetail()
    }

    private fun fetchGroupDetail() {
        groupId?.let {
            groupViewModel?.fetchGroupDetails(it)
        }
    }

    private fun fetchGroupNickname() {
        groupId?.let {
            groupViewModel?.fetchGroupMemberAllAttributes(
                it,
                mutableListOf(ChatClient.getInstance().currentUser),
                mutableListOf(EaseConstant.GROUP_MEMBER_ATTRIBUTE_NICKNAME)
            )
        }
    }

    private fun updateView(){
        binding.tvName.text = group?.groupName ?: groupId
        binding.tvNumber.text = resources.getString(R.string.ease_group_detail_group_id, group?.groupId ?: groupId)
        group?.description?.let {
            if (it.isNotEmpty()){
                binding.tvSignature.visibility = View.VISIBLE
                binding.tvSignature.text = group?.description
            }else{
                binding.tvSignature.visibility = View.GONE
            }
        }
        binding.itemMemberList.tvContent?.text = group?.memberCount?.toString()
        EaseIM.getGroupProfileProvider()?.getSyncProfile(group?.groupId).let {
            it?.let {
                it.avatar?.let {
                    binding.ivGroupAvatar.load(it) {
                        error(R.drawable.ease_default_group_avatar)
                        placeholder(R.drawable.ease_default_group_avatar)
                    }
                }
            }
        }
        if (group?.isOwner() == true){
            binding.itemSpacing.visibility = View.VISIBLE
            binding.itemGroupName.visibility = View.VISIBLE
            binding.itemGroupDescribe.visibility = View.VISIBLE
            binding.itemGroupName.tvContent?.text = group?.groupName
            binding.itemGroupDescribe.tvContent?.text = group?.description
        }else{
            binding.itemSpacing.visibility = View.GONE
            binding.itemGroupName.visibility = View.GONE
            binding.itemGroupDescribe.visibility = View.GONE
        }
    }

    override fun onCheckedChanged(buttonView: EaseSwitchItemView?, isChecked: Boolean) {
        when(buttonView?.id) {
            R.id.switch_item_disturb -> {
                groupId?.let {
                    if (isChecked){
                        groupViewModel?.makeSilentModeForConversation(it,ChatConversationType.GroupChat)
                    }else{
                        groupViewModel?.cancelSilentForConversation(it,ChatConversationType.GroupChat)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.item_clear -> {
                val dialog = CustomDialog(
                    context = this@EaseGroupDetailActivity,
                    title = resources.getString(R.string.ease_dialog_clear),
                    isEditTextMode = false,
                    onLeftButtonClickListener = {

                    },
                    onRightButtonClickListener = {
                        groupViewModel?.clearConversationMessage(groupId)
                    }
                )
                dialog.show()
            }
            R.id.item_member_list -> {
                groupId?.let {
                    startActivity(
                        EaseGroupMembersListActivity.createIntent(
                            context = this,
                            groupId = it,
                        )
                    )
                }
            }
            R.id.item_group_name -> {
                groupId?.let {
                    startActivity(EaseGroupDetailEditActivity.createIntent(
                        this,it,EditType.ACTION_EDIT_GROUP_NAME)
                    )
                }
            }
            R.id.item_group_describe -> {
                groupId?.let {
                    startActivity(EaseGroupDetailEditActivity.createIntent(
                        this,it,EditType.ACTION_EDIT_GROUP_DESCRIBE)
                    )
                }
            }
            R.id.tv_number -> {
                copyGroupId()
            }
            else -> {}
        }
    }

    open fun copyGroupId(){
        val indexOfSpace = binding.tvNumber.text.indexOf(":")
        if (indexOfSpace != -1) {
            val substring = binding.tvNumber.text.substring(indexOfSpace + 1)
            clipboard.setPrimaryClip(
                ClipData.newPlainText(
                    null,
                    substring
                )
            )
        }
    }

    open fun getBottomSheetMenu(): MutableList<EaseMenuItem>?{
        val mutableListOf = if (group?.isOwner() == true){
            EaseBottomMenuConfig(this).getDefaultGroupOwnerBottomSheetMenu()
        }else{
            EaseBottomMenuConfig(this).getDefaultGroupBottomSheetMenu()
        }
        return mutableListOf
    }

    private fun showDialog(){
        val context = this@EaseGroupDetailActivity
        dialog = SimpleListSheetDialog(
            context = context,
            itemList = getBottomSheetMenu(),
            itemListener = object : SimpleListSheetItemClickListener {
                override fun onItemClickListener(position: Int, menu: EaseMenuItem) {
                    simpleMenuItemClickListener(position, menu)
                    dialog?.dismiss()
                }
            })
        supportFragmentManager.let { dialog?.show(it,"group_more_dialog") }
    }

    open fun simpleMenuItemClickListener(position: Int,menu: EaseMenuItem){
        when(menu.menuId){
            R.id.bottom_sheet_item_change_owner -> {
                if (group?.isOwner() == true){
                    changeOwner()
                }
            }
            R.id.bottom_sheet_item_destroy_group -> {
                leaveOrDestroy()
            }
            R.id.bottom_sheet_item_leave_group -> {
                leaveOrDestroy()
            }
            else -> {}
        }
    }

    open fun changeOwner(){
        groupId?.let {
            startActivity(EaseGroupMembersListActivity.createIntent(
                this@EaseGroupDetailActivity,
                it, EaseGroupMemberType.GROUP_MEMBER_CHANGE_OWNER)
            )
        }
    }

    open fun leaveOrDestroy(){
        val dialogDelete = CustomDialog(
            context = this@EaseGroupDetailActivity,
            title = resources.getString(
                if (group?.isOwner() == true) R.string.ease_group_detail_leave_dialog_destroy else
                    R.string.ease_group_detail_leave_dialog
            ),
            subtitle = resources.getString(
                if (group?.isOwner() == true) R.string.ease_group_detail_leave_dialog_owner_des
                else R.string.ease_group_detail_leave_dialog_des),
            isEditTextMode = false,
            onLeftButtonClickListener = {
                dialog?.dismiss()
            },
            onRightButtonClickListener = {
                groupId?.let {
                    if (group?.isOwner() == true){
                        groupViewModel?.destroyChatGroup(it)
                    }else{
                        groupViewModel?.leaveChatGroup(it)
                    }
                }
                dialog?.dismiss()
            }
        )
        dialogDelete.show()
    }

    override fun fetchGroupDetailSuccess(group: ChatGroup) {
        this.group = group
        updateView()
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.UPDATE.name)
            .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.UPDATE.name, EaseEvent.TYPE.GROUP, groupId))
    }

    override fun fetchGroupDetailFail(code: Int, error: String) {
        ChatLog.e(TAG,"fetchGroupDetailFail $code $error")
    }

    override fun leaveChatGroupSuccess() {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.LEAVE.name)
            .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.LEAVE.name, EaseEvent.TYPE.GROUP, groupId))
        finish()
    }

    override fun leaveChatGroupFail(code: Int, error: String) {
        ChatLog.e(TAG,"leaveChatGroupFail $code $error")
    }

    override fun destroyChatGroupSuccess() {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.DESTROY.name)
            .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.DESTROY.name, EaseEvent.TYPE.GROUP, groupId))
        finish()
    }

    override fun destroyChatGroupFail(code: Int, error: String) {
        ChatLog.e(TAG,"destroyChatGroupFail $code $error")
    }

    override fun clearConversationByGroupSuccess(conversationId: String?) {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE.name)
            .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.REMOVE.name, EaseEvent.TYPE.CONVERSATION, conversationId))
    }

    override fun clearConversationByGroupFail(code: Int, error: String?) {
        ChatLog.e(TAG,"deleteConversationByGroupFail $code $error")
    }

    override fun makeSilentForGroupSuccess(silentResult: ChatSilentModeResult) {
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.UPDATE.name)
            .post(mContext.mainScope()
                , EaseEvent(EaseEvent.EVENT.UPDATE.name
                    , EaseEvent.TYPE.SILENT, groupId))
    }

    override fun makeSilentForGroupFail(
        code: Int,
        error: String?
    ) {
        ChatLog.e(TAG,"makeSilentForGroupFail $code $error")
    }

    override fun cancelSilentForGroupSuccess() {
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.UPDATE.name)
            .post(mContext.mainScope()
                , EaseEvent(EaseEvent.EVENT.UPDATE.name
                    , EaseEvent.TYPE.CONVERSATION, groupId))
    }

    override fun cancelSilentForGroupFail(code: Int, error: String?) {
        ChatLog.e(TAG,"cancelSilentForGroupFail $code $error")
    }

    override fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean {
        item?.let {menu->
            when(menu.menuId){
                R.id.extend_item_message -> {
                    groupId?.let {
                        EaseChatActivity.actionStart(mContext, it, EaseChatType.GROUP_CHAT)
                    }
                }
                R.id.extend_item_search -> {
                    groupId?.let {
                        mContext.startActivity(EaseSearchActivity.createIntent(mContext, EaseSearchType.MESSAGE,it))
                    }
                }
                else -> {}
            }
            return true
        }
        return false
    }
}