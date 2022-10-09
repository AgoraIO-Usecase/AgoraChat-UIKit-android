package io.agora.chat.uikit.manager;

import android.content.Context;
import android.util.Log;

import java.util.List;

import io.agora.ChatThreadChangeListener;
import io.agora.MessageListener;
import io.agora.MultiDeviceListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThreadEvent;
import io.agora.chat.Conversation;
import io.agora.chat.GroupReadAck;
import io.agora.chat.MessageReactionChange;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseUserUtils;

public class EaseChatPresenter implements MessageListener, ChatThreadChangeListener, MultiDeviceListener {
    private static final String TAG = EaseChatPresenter.class.getSimpleName();
    public Context context;

    public EaseChatPresenter() {
        ChatClient.getInstance().chatManager().addMessageListener(this);
        //Add thread change listener
        ChatClient.getInstance().chatThreadManager().addChatThreadChangeListener(this);
        //Add multi-terminal login monitoring
        ChatClient.getInstance().addMultiDeviceListener(this);
    }


    public void attachApp(Context context) {
        this.context = context;
    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageReceived(List<ChatMessage> messages) {
        Log.e(TAG, "EaseChatPresenter onMessageReceived messages.size = "+messages.size());
        EaseAtMessageHelper.get().parseMessages(messages);
    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<ChatMessage> messages) {

    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageRead(List<ChatMessage> messages) {

    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageDelivered(List<ChatMessage> messages) {

    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageRecalled(List<ChatMessage> messages) {
        
    }

    /**
     * ChatMessageListener
     * @param message
     * @param change
     */
    @Override
    public void onMessageChanged(ChatMessage message, Object change) {

    }

    @Override
    public void onGroupMessageRead(List<GroupReadAck> groupReadAcks) {
        for (GroupReadAck ack : groupReadAcks) {
            EaseDingMessageHelper.get().handleGroupReadAck(ack);
        }
    }

    public EaseNotifier getNotifier() {
        return EaseUIKit.getInstance().getNotifier();
    }

    @Override
    public void onChatThreadCreated(ChatThreadEvent event) {
        createThreadCreatedMsg(event);
    }

    @Override
    public void onChatThreadUpdated(ChatThreadEvent event) {

    }

    @Override
    public void onChatThreadDestroyed(ChatThreadEvent event) {
        Conversation conversation = ChatClient.getInstance().chatManager().getConversation(event.getChatThread().getParentId());
        if(conversation != null) {
            conversation.removeMessage(event.getChatThread().getChatThreadId());
        }
    }

    @Override
    public void onChatThreadUserRemoved(ChatThreadEvent event) {

    }

    @Override
    public void onContactEvent(int event, String target, String ext) {

    }

    @Override
    public void onGroupEvent(int event, String target, List<String> usernames) {

    }

    @Override
    public void onChatThreadEvent(int event, String target, List<String> usernames) {
        if(event == THREAD_DESTROY || event == THREAD_LEAVE) {
            ChatMessage message = ChatClient.getInstance().chatManager().getMessage(target);
            if(message != null) {
                Conversation conversation = ChatClient.getInstance().chatManager().getConversation(message.conversationId());
                if(conversation != null) {
                    conversation.removeMessage(target);
                }
            }
        }
    }

    private void createThreadCreatedMsg(ChatThreadEvent event) {
        ChatMessage msg = ChatMessage.createReceiveMessage(ChatMessage.Type.TXT);
        msg.setChatType(ChatMessage.ChatType.GroupChat);
        msg.setFrom(event.getFrom());
        msg.setTo(event.getChatThread().getParentId());
        // Set the thread id to the message id for easy removal later
        msg.setMsgId(event.getChatThread().getChatThreadId());
        msg.setAttribute(EaseConstant.EASE_THREAD_NOTIFICATION_TYPE, true);
        msg.setAttribute(EaseConstant.EASE_THREAD_PARENT_MSG_ID, event.getChatThread().getMessageId());
        StringBuilder builder = new StringBuilder();
        EaseUser userInfo = EaseUserUtils.getUserInfo(event.getFrom());
        builder.append(userInfo != null ? userInfo.getNickname() : event.getFrom());
        builder.append(" ");
        builder.append(context.getResources().getString(R.string.ease_start_a_thread));
        builder.append(event.getChatThread().getChatThreadName());
        builder.append("\n");
        builder.append(context.getResources().getString(R.string.ease_join_the_thread));
        msg.addBody(new TextMessageBody(builder.toString()));
        msg.setStatus(ChatMessage.Status.SUCCESS);
        ChatClient.getInstance().chatManager().saveMessage(msg);
    }

    @Override
    public void onReactionChanged(List<MessageReactionChange> list) {
    }
}
