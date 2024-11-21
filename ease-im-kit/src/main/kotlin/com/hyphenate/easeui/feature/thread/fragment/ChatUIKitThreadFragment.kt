package com.hyphenate.easeui.feature.thread.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.common.ChatThreadEvent
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.bus.ChatUIKitFlowBus
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.dialog.SimpleListSheetDialog
import com.hyphenate.easeui.common.extensions.isAdmin
import com.hyphenate.easeui.common.extensions.isOwner
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.feature.chat.UIKitChatFragment
import com.hyphenate.easeui.feature.chat.chathistory.ChatUIKitHistoryAdapter
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitType
import com.hyphenate.easeui.feature.group.ChatUIKitGroupDetailEditActivity
import com.hyphenate.easeui.feature.group.EditType
import com.hyphenate.easeui.feature.thread.ChatUIKitThreadMemberActivity
import com.hyphenate.easeui.feature.thread.interfaces.IChatThreadResultView
import com.hyphenate.easeui.feature.thread.interfaces.OnChatThreadRoleResultCallback
import com.hyphenate.easeui.feature.thread.interfaces.OnJoinChatThreadResultListener
import com.hyphenate.easeui.feature.thread.widgets.ChatUIKitThreadRole
import com.hyphenate.easeui.interfaces.SimpleListSheetItemClickListener
import com.hyphenate.easeui.model.ChatUIKitEvent
import com.hyphenate.easeui.model.ChatUIKitMenuItem
import com.hyphenate.easeui.viewmodel.thread.ChatUIKitThreadViewModel
import com.hyphenate.easeui.viewmodel.thread.IChatThreadRequest

open class ChatUIKitThreadFragment:UIKitChatFragment(),IChatThreadResultView{
    protected var topicMsgId:String = ""
    protected var parentId:String = ""
    protected var chatThreadId:String? = ""
    private var msgId:String? = null
    private var message:ChatMessage? = null
    private var topicMsg:ChatMessage? = null
    protected var mThread:ChatThread? = null
    protected var threadRole: ChatUIKitThreadRole = ChatUIKitThreadRole.UNKNOWN
    private var viewModel: IChatThreadRequest? = null
    protected var parentInfo:ChatGroup? = null
    private var isJoinSuccess = false
    private var moreDialog:SimpleListSheetDialog? = null

    private var joinChatThreadResultListener:OnJoinChatThreadResultListener? = null
    private var threadRoleResultListener:OnChatThreadRoleResultCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initArguments()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initArguments() {
        arguments?.let {
            parentId = it.getString(ChatUIKitConstant.THREAD_PARENT_ID,"")
            chatThreadId = it.getString(ChatUIKitConstant.THREAD_CHAT_THREAD_ID)
            topicMsgId = it.getString(ChatUIKitConstant.THREAD_TOPIC_MESSAGE_ID,"")
            msgId = it.getString(ChatUIKitConstant.THREAD_MESSAGE_ID)
            message = ChatClient.getInstance().chatManager().getMessage(msgId)

            parentInfo = ChatClient.getInstance().groupManager().getGroup(parentId)
            topicMsg = ChatClient.getInstance().chatManager().getMessage(topicMsgId)

            topicMsg?.let { msg->
                mThread = msg.chatThread
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val chatThreadName = mThread?.chatThreadName?:""
        updateHeader(chatThreadName)
        defaultTopicView()
    }

    private fun updateHeader(threadName:String?){
        binding?.run {
            titleBar.getLogoView()?.visibility = View.GONE
            titleBar.setTitle(threadName)
            titleBar.setSubtitle(mContext.getString(R.string.uikit_thread_affiliation_group,parentInfo?.groupName?:parentId))
        }
    }

    private fun defaultTopicView(){
        binding?.run {
            val topicMsg = ChatClient.getInstance().chatManager().getMessage(topicMsgId)
            topicMsg?.let {
                val topicAdapter = ChatUIKitHistoryAdapter()
                topicAdapter.setHasStableIds(true)
                layoutChat.chatMessageListLayout?.addHeaderAdapter(topicAdapter)
                topicAdapter.setData(mutableListOf(it))
            }
        }
    }

    fun destroyChatThread(){
        parentInfo?.let {
            if (it.isOwner()){
                mThread?.chatThreadId?.let { id -> viewModel?.destroyChatThread(id) }
            }
        }
    }

    fun leaveChatThread(){
        mThread?.let {
            viewModel?.leaveChatThread(it.chatThreadId)
        }
    }

    override fun initData() {
        binding?.layoutChat?.setParentId(parentId)
        super.initData()
        joinThread()
        setGroupInfo()
    }

    override fun initEventBus() {
        super.initEventBus()
        ChatUIKitFlowBus.with<ChatUIKitEvent>(ChatUIKitEvent.EVENT.UPDATE.name).register(viewLifecycleOwner) {
            if (it.isThreadChange) {
                topicMsg?.let { msg->
                    mThread = msg.chatThread
                }
                updateHeader(it.message)
            }
        }
    }

    override fun loadData() {
        if (isJoinSuccess) {
            binding?.layoutChat?.chatMessageListLayout?.getMessagesAdapter()?.setParentInfo(parentId, topicMsgId)
            super.loadData()
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel = ViewModelProvider(this)[ChatUIKitThreadViewModel::class.java]
        viewModel?.attachView(this)
        if (parentId.isNotEmpty() && topicMsgId.isNotEmpty()){
            viewModel?.setupWithToConversation(parentId,topicMsgId)
        }
    }

    override fun initListener() {
        super.initListener()
        binding?.let {
            it.titleBar.inflateMenu(R.menu.menu_action_more)
            it.titleBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_more -> {
                        showDialog()
                    }
                }
                true
            }
        }
    }

    open fun showDialog(){
        context?.let {
            val menu = mutableListOf(
                ChatUIKitMenuItem(
                    order = 0,
                    menuId = R.id.thread_more_edit,
                    title = resources.getString(R.string.thread_edit),
                    resourceId = R.drawable.icon_thread_edit,
                    titleColor = ContextCompat.getColor(mContext, R.color.ease_color_on_background),
                    isVisible = (parentInfo?.isOwner() == true || parentInfo?.isAdmin() == true || mThread?.isOwner() == true)
                ),
                ChatUIKitMenuItem(
                    order = 1*10,
                    menuId = R.id.thread_more_member,
                    title = resources.getString(R.string.thread_member),
                    resourceId = R.drawable.icon_thread_member,
                    titleColor = ContextCompat.getColor(mContext, R.color.ease_color_on_background)
                ),
                ChatUIKitMenuItem(
                    order = 2*10,
                    menuId = R.id.thread_more_leave,
                    title =  resources.getString(R.string.thread_leave),
                    resourceId = R.drawable.icon_thread_leave,
                    titleColor = ContextCompat.getColor(mContext, R.color.ease_color_on_background),
                ),
                ChatUIKitMenuItem(
                    order = 3*10,
                    menuId = R.id.thread_more_destroy,
                    title =  resources.getString(R.string.thread_destroy) ,
                    resourceId = R.drawable.icon_thread_destroy ,
                    titleColor = ContextCompat.getColor(mContext, R.color.ease_color_error),
                    isVisible = (parentInfo?.isOwner() == true || parentInfo?.isAdmin() == true)
                )
            )
            moreDialog = SimpleListSheetDialog(
                context = it,
                itemList = menu,
                itemListener = object : SimpleListSheetItemClickListener {
                    override fun onItemClickListener(position: Int, menu: ChatUIKitMenuItem) {
                        when(menu.menuId){
                            R.id.thread_more_edit -> {
                                startActivity(
                                    ChatUIKitGroupDetailEditActivity.createIntent(
                                    context = mContext,
                                    groupId = mThread?.parentId,
                                    type = EditType.ACTION_EDIT_THREAD_NAME,
                                    threadName = mThread?.chatThreadName,
                                    threadId = mThread?.chatThreadId
                                ))
                                moreDialog?.dismiss()
                            }
                            R.id.thread_more_member -> {
                                chatThreadId?.let { threadId ->
                                    ChatUIKitThreadMemberActivity.actionStart(mContext,parentId,threadId)
                                }
                                moreDialog?.dismiss()
                            }
                            R.id.thread_more_leave -> {
                                leaveChatThread()
                            }
                            R.id.thread_more_destroy -> {
                                showDestroyChatThreadDialog()
                            }
                            else -> {}
                        }
                    }
                })
            parentFragmentManager.let { pm-> moreDialog?.show(pm,"thread_more_dialog") }
        }

    }

    open fun showDestroyChatThreadDialog() {
        val clearDialog = CustomDialog(
            context = mContext,
            title = mContext.resources.getString(R.string.uikit_thread_delete_topic_title),
            subtitle = mContext.resources.getString(R.string.uikit_thread_delete_topic_subtitle),
            isEditTextMode = false,
            onLeftButtonClickListener = {

            },
            onRightButtonClickListener = {
                destroyChatThread()
            }
        )
        clearDialog.show()
    }

    private fun setGroupInfo() {
        parentId.let {
            viewModel?.setGroupInfo(it)
        }
    }

    private fun setOnJoinThreadResultListener(listener: OnJoinChatThreadResultListener?){
        this.joinChatThreadResultListener = listener
    }

    private fun setOnThreadRoleResultCallback(listener:OnChatThreadRoleResultCallback?){
        this.threadRoleResultListener = listener
    }

    override fun joinChatThreadSuccess(chatThread: ChatThread) {
        isJoinSuccess = true
        joinChatThreadResultListener?.joinSuccess(chatThread.chatThreadId)
        mThread = chatThread
        updateHeader(mThread?.chatThreadName)
        getThreadRole(mThread)
        if (threadRole !== ChatUIKitThreadRole.GROUP_ADMIN && threadRole !== ChatUIKitThreadRole.CREATOR) {
            threadRole = ChatUIKitThreadRole.MEMBER
            if (threadRoleResultListener != null) {
                threadRoleResultListener?.onThreadRole(threadRole)
            }
        }
        sendMessage()
        loadData()
    }

    override fun joinChatThreadFail(code: Int, error: String?) {
        if (code == ChatError.USER_ALREADY_EXIST) {
            isJoinSuccess = true
            // If has joined the chat thread, make the role to member
            if (threadRole === ChatUIKitThreadRole.UNKNOWN) {
                threadRole = ChatUIKitThreadRole.MEMBER
            }
            chatThreadId?.let { viewModel?.fetchChatThreadFromServer(it) }
            sendMessage()
            loadData()
        } else {
            isJoinSuccess = false
            if (joinChatThreadResultListener != null) {
                joinChatThreadResultListener?.joinFailed(code, error)
            }
        }
    }

    private fun sendMessage(){
        message?.let {msg->
            chatThreadId?.let {
                binding?.layoutChat?.sendMessage(msg)
            }
        }
    }

    private fun getThreadRole(thread: ChatThread?): ChatUIKitThreadRole {
        if (threadRole === ChatUIKitThreadRole.GROUP_ADMIN) {
            return threadRole
        }
        if (thread != null) {
            if (TextUtils.equals(thread.owner, ChatUIKitClient.getCurrentUser()?.id)) {
                threadRole = ChatUIKitThreadRole.CREATOR
            }
        }
        if (threadRoleResultListener != null) {
            threadRoleResultListener?.onThreadRole(threadRole)
        }
        return threadRole
    }

    private fun joinThread(){
        chatThreadId?.let { viewModel?.joinChatThread(it) }
    }

    override fun leaveChatThreadSuccess() {
        mContext.finish()
    }

    override fun leaveChatThreadFail(code: Int, message: String?) {
        ChatLog.e(TAG,"leaveChatThreadFail $code $message")
    }

    override fun destroyChatThreadSuccess() {
        ChatUIKitFlowBus.with<ChatUIKitEvent>(ChatUIKitEvent.EVENT.DESTROY.name)
            .post(mContext.mainScope(), ChatUIKitEvent(ChatUIKitEvent.EVENT.DESTROY.name, ChatUIKitEvent.TYPE.THREAD))
        mContext.finish()
    }

    override fun destroyChatThreadFail(code: Int, message: String?) {
        ChatLog.e(TAG,"destroyChatThreadFail $code $message")
    }

    override fun fetchChatThreadDetailsFromServerSuccess(chatThread: ChatThread) {
        ChatLog.e(TAG,"fetchChatThreadDetailsFromServerSuccess")
        mThread = chatThread
        updateHeader(mThread?.chatThreadName)
    }

    override fun fetchChatThreadDetailsFromServerFail(code: Int, message: String?) {
        ChatLog.e(TAG,"fetchChatThreadDetailsFromServerFail $code $message")
    }

    override fun settGroupInfoSuccess(parent: ChatGroup?) {
        ChatLog.e(TAG,"getThreadParentInfoSuccess")
        parentInfo = parent
    }

    override fun setGroupInfoFail(code: Int, message: String?) {
        ChatLog.e(TAG,"getThreadParentInfoFail $code $message")
    }

    override fun onChatThreadUpdated(event: ChatThreadEvent?) {
        event?.chatThread?.let {
            if (conversationId == event.chatThread.parentId){
                binding?.titleBar?.setTitle(it.chatThreadName?:"")
            }
        }
    }

    override fun onChatThreadDestroyed(event: ChatThreadEvent?) {
        event?.chatThread?.let {
            exitThreadChat(it.chatThreadId)
        }
    }

    override fun onChatThreadUserRemoved(event: ChatThreadEvent?) {
        event?.chatThread?.let {
            exitThreadChat(it.chatThreadId)
        }
    }

    open fun exitThreadChat(threadId: String) {
        if (TextUtils.equals(threadId, conversationId)) {
            mContext.finish()
        }
    }

    class Builder(
        private val parentId: String?,
        private val threadId:String?,
        private val topicMsgId: String?,
        private val msgId:String? = null,
    ):UIKitChatFragment.Builder(threadId,ChatUIKitType.GROUP_CHAT) {
        private var joinChatThreadResultListener:OnJoinChatThreadResultListener? = null
        private var threadRoleResultListener:OnChatThreadRoleResultCallback? = null

        init {
            bundle.putString(ChatUIKitConstant.THREAD_PARENT_ID,parentId)
            bundle.putString(ChatUIKitConstant.THREAD_CHAT_THREAD_ID,threadId)
            bundle.putString(ChatUIKitConstant.THREAD_TOPIC_MESSAGE_ID,topicMsgId)
            bundle.putString(ChatUIKitConstant.THREAD_MESSAGE_ID,msgId)
        }

        fun setOnJoinThreadResultListener(listener: OnJoinChatThreadResultListener?): Builder{
            this.joinChatThreadResultListener = listener
            return this
        }

        fun setOnThreadRoleResultCallback(listener:OnChatThreadRoleResultCallback):Builder{
            this.threadRoleResultListener = listener
            return this
        }

        override fun build(): UIKitChatFragment? {
            if (customFragment == null) {
                customFragment = ChatUIKitThreadFragment()
            }
            if (customFragment is ChatUIKitThreadFragment){
                (customFragment as ChatUIKitThreadFragment).setOnJoinThreadResultListener(joinChatThreadResultListener)
                (customFragment as ChatUIKitThreadFragment).setOnThreadRoleResultCallback(threadRoleResultListener)
            }
            setThreadMessage(true)
            return super.build()
        }

    }

    companion object{
        private val TAG = ChatUIKitThreadFragment::class.java.simpleName
    }
}