package io.agora.chat.uikit.chatthread.presenter;

import android.net.Uri;
import android.text.TextUtils;

import io.agora.CallBack;
import io.agora.Error;
import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.Group;
import io.agora.chat.MessageBody;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.manager.EaseAtMessageHelper;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.util.EMLog;

public class EaseChatThreadCreatePresenterImpl extends EaseChatThreadCreatePresenter {
    private static final String TAG = EaseChatThreadCreatePresenterImpl.class.getSimpleName();

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
        setMessage(message);
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
        setMessage(message);
    }

    @Override
    public void sendBigExpressionMessage(String name, String identityCode) {
        ChatMessage message = EaseUtils.createExpressionMessage(toChatUsername, name, identityCode);
        setMessage(message);
    }

    @Override
    public void sendVoiceMessage(Uri filePath, int length) {
        ChatMessage message = ChatMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        setMessage(message);
    }

    @Override
    public void sendImageMessage(Uri imageUri) {
        sendImageMessage(imageUri, false);
    }

    @Override
    public void sendGroupDingMessage(ChatMessage message) {
        setMessage(message);
    }

    @Override
    public void sendImageMessage(Uri imageUri, boolean sendOriginalImage) {
        ChatMessage message = ChatMessage.createImageSendMessage(imageUri, sendOriginalImage, toChatUsername);
        setMessage(message);
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
        setMessage(message);
    }

    @Override
    public void sendVideoMessage(Uri videoUri, int videoLength) {
        String thumbPath = EaseFileUtils.getThumbPath(mView.context(), videoUri);
        ChatMessage message = ChatMessage.createVideoSendMessage(videoUri, thumbPath, videoLength, toChatUsername);
        setMessage(message);
    }

    @Override
    public void sendFileMessage(Uri fileUri) {
        ChatMessage message = ChatMessage.createFileSendMessage(fileUri, toChatUsername);
        setMessage(message);
    }

    @Override
    public void addMessageAttributes(ChatMessage message) {
        //You can add some custom attributes
        mView.addMsgAttrBeforeSend(message);
    }

    @Override
    public void createThread(String threadName, ChatMessage message) {
        if(TextUtils.isEmpty(threadName)) {
            mView.onCreateThreadFail(Error.GENERAL_ERROR, "Thread name should not be null");
            return;
        }
        ChatClient.getInstance().chatThreadManager().createChatThread(parentId, messageId, threadName, new ValueCallBack<ChatThread>() {
            @Override
            public void onSuccess(ChatThread value) {
                toChatUsername = value.getChatThreadId();
                if(isActive()) {
                    runOnUI(()->mView.onCreateThreadSuccess(value, message));
                    EMLog.d("createChatThread","onSuccess");
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(isActive()) {
                    runOnUI(()->mView.onCreateThreadFail(error, errorMsg));
                    EMLog.e("createChatThread","onError: " + error + "  " + errorMsg);
                }
            }
        });
    }

    @Override
    public void sendMessage(ChatMessage message) {
        if(message == null) {
            if(isActive()) {
                runOnUI(() -> mView.sendMessageFail("message is null!"));
            }
            return;
        }
        if(TextUtils.isEmpty(message.getTo())) {
            message.setTo(toChatUsername);
        }
        addMessageAttributes(message);
        if (chatType == EaseChatType.GROUP_CHAT){
            message.setChatType(ChatMessage.ChatType.GroupChat);
        }else if(chatType == EaseChatType.CHATROOM){
            message.setChatType(ChatMessage.ChatType.ChatRoom);
        }
        // Add thread label for message
        message.setIsChatThreadMessage(true);
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

    public void setMessage(ChatMessage message) {
        createThread(etInput == null ? "" : etInput.getText().toString().trim(), message);
    }
}
