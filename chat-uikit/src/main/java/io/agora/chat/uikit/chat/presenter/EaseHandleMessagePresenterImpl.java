package io.agora.chat.uikit.chat.presenter;

import android.net.Uri;
import android.text.TextUtils;

import io.agora.CallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.CmdMessageBody;
import io.agora.chat.Group;
import io.agora.chat.MessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.EaseChatLayout;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.manager.EaseAtMessageHelper;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.exceptions.ChatException;
import io.agora.util.EMLog;

public class EaseHandleMessagePresenterImpl extends EaseHandleMessagePresenter {
    private static final String TAG = EaseChatLayout.class.getSimpleName();

    @Override
    public void sendTextMessage(String content) {
        if(TextUtils.isEmpty(content)) {
            EMLog.e(TAG, "sendTextMessage : message content is empty");
            return;
        }
        sendTextMessage(content, false);
    }

    @Override
    public void sendTextMessage(String content, boolean isNeedGroupAck) {
        if(EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
            return;
        }
        ChatMessage message = ChatMessage.createTxtSendMessage(content, toChatUsername);
        message.setIsNeedGroupAck(isNeedGroupAck);
        sendMessage(message);
    }

    @Override
    public void sendAtMessage(String content) {
        if(!isGroupChat()){
            EMLog.e(TAG, "only support group chat message");
            if(isActive()) {
                runOnUI(()-> mView.sendMessageFail("only support group chat message"));
            }
            return;
        }
        ChatMessage message = ChatMessage.createTxtSendMessage(content, toChatUsername);
        Group group = ChatClient.getInstance().groupManager().getGroup(toChatUsername);
        if(ChatClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)){
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        }else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);
    }

    @Override
    public void sendBigExpressionMessage(String name, String identityCode) {
        ChatMessage message = EaseUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    @Override
    public void sendVoiceMessage(Uri filePath, int length) {
        ChatMessage message = ChatMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    @Override
    public void sendImageMessage(Uri imageUri) {
        sendImageMessage(imageUri, false);
    }

    @Override
    public void sendImageMessage(Uri imageUri, boolean sendOriginalImage) {
        ChatMessage message = ChatMessage.createImageSendMessage(imageUri, sendOriginalImage, toChatUsername);
        sendMessage(message);
    }

    @Override
    public void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        ChatMessage message = ChatMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        EMLog.i(TAG, "current = "+ChatClient.getInstance().getCurrentUser() + " to = "+toChatUsername);
        MessageBody body = message.getBody();
        String msgId = message.getMsgId();
        String from = message.getFrom();
        EMLog.i(TAG, "body = "+body);
        EMLog.i(TAG, "msgId = "+msgId + " from = "+from);
        sendMessage(message);
    }

    @Override
    public void sendVideoMessage(Uri videoUri, int videoLength) {
        String thumbPath = getThumbPath(videoUri);
        ChatMessage message = ChatMessage.createVideoSendMessage(videoUri, thumbPath, videoLength, toChatUsername);
        sendMessage(message);
    }

    @Override
    public void sendFileMessage(Uri fileUri) {
        ChatMessage message = ChatMessage.createFileSendMessage(fileUri, toChatUsername);
        sendMessage(message);
    }

    @Override
    public void addMessageAttributes(ChatMessage message) {
        //You can add some custom attributes
        mView.addMsgAttrBeforeSend(message);
    }

    @Override
    public void sendMessage(ChatMessage message) {
        if(message == null) {
            if(isActive()) {
                runOnUI(() -> mView.sendMessageFail("message is null!"));
            }
            return;
        }
        addMessageAttributes(message);
        if (chatType == EaseChatType.GROUP_CHAT){
            message.setChatType(ChatMessage.ChatType.GroupChat);
        }else if(chatType == EaseChatType.CHATROOM){
            message.setChatType(ChatMessage.ChatType.ChatRoom);
        }
        // Should add thread label if it is a thread conversation
        message.setIsChatThreadMessage(isThread);
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                if(isActive()) {
                    runOnUI(()-> mView.onPresenterMessageSuccess(message));
                }
            }

            @Override
            public void onError(int code, String error) {
                if(isActive()) {
                    runOnUI(()-> mView.onPresenterMessageError(message, code, error));
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                if(isActive()) {
                    runOnUI(()-> mView.onPresenterMessageInProgress(message, progress));
                }
            }
        });
        // send message
        ChatClient.getInstance().chatManager().sendMessage(message);
        if(isActive()) {
            runOnUI(()-> mView.sendMessageFinish(message));
        }
    }

    @Override
    public void sendCmdMessage(String action) {
        ChatMessage beginMsg = ChatMessage.createSendMessage(ChatMessage.Type.CMD);
        CmdMessageBody body = new CmdMessageBody(action);
        // Only deliver this cmd msg to online users
        body.deliverOnlineOnly(true);
        beginMsg.addBody(body);
        beginMsg.setTo(toChatUsername);
        ChatClient.getInstance().chatManager().sendMessage(beginMsg);
    }

    @Override
    public void resendMessage(ChatMessage message) {
        message.setStatus(ChatMessage.Status.CREATE);
        long currentTimeMillis = System.currentTimeMillis();
        message.setLocalTime(currentTimeMillis);
        message.setMsgTime(currentTimeMillis);
        ChatClient.getInstance().chatManager().updateMessage(message);
        sendMessage(message);
    }

    @Override
    public void deleteMessage(ChatMessage message) {
        conversation.removeMessage(message.getMsgId());
        if(isActive()) {
            runOnUI(()->mView.deleteLocalMessageSuccess(message));
        }
    }

    @Override
    public void recallMessage(ChatMessage message) {
        try {
            ChatMessage msgNotification = ChatMessage.createSendMessage(ChatMessage.Type.TXT);
            TextMessageBody txtBody = new TextMessageBody(mView.context().getResources().getString(R.string.ease_msg_recall_by_self));
            msgNotification.addBody(txtBody);
            msgNotification.setTo(message.getTo());
            msgNotification.setMsgTime(message.getMsgTime());
            msgNotification.setLocalTime(message.getMsgTime());
            msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
            msgNotification.setStatus(ChatMessage.Status.SUCCESS);
            msgNotification.setIsChatThreadMessage(message.isChatThreadMessage());
            ChatClient.getInstance().chatManager().recallMessage(message);
            ChatClient.getInstance().chatManager().saveMessage(msgNotification);
            if(isActive()) {
                runOnUI(()->mView.recallMessageFinish(message, msgNotification));
            }
        } catch (ChatException e) {
            e.printStackTrace();
            if(isActive()) {
                runOnUI(()->mView.recallMessageFail(e.getErrorCode(), e.getDescription()));
            }
        }
    }

    private String getThumbPath(Uri videoUri) {
        return EaseFileUtils.getThumbPath(mView.context(), videoUri);
    }

    @Override
    public void addReaction(ChatMessage message, String reaction) {
        ChatClient.getInstance().chatManager().asyncAddReaction(message.getMsgId(), reaction, new CallBack() {
            @Override
            public void onSuccess() {
                if (isActive()) {
                    runOnUI(() -> mView.addReactionMessageSuccess(message));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isActive()) {
                    runOnUI(() -> mView.addReactionMessageFail(message, error, errorMsg));
                }
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @Override
    public void removeReaction(ChatMessage message, String reaction) {
        ChatClient.getInstance().chatManager().asyncRemoveReaction(message.getMsgId(), reaction, new CallBack() {
            @Override
            public void onSuccess() {
                if (isActive()) {
                    runOnUI(() -> mView.removeReactionMessageSuccess(message));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isActive()) {
                    runOnUI(() -> mView.removeReactionMessageFail(message, error, errorMsg));
                }
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}

