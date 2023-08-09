package io.agora.chat.uikit.chathistory.presenter;


import java.util.List;

import io.agora.CallBack;
import io.agora.Error;
import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;


public class EaseChatHistoryPresenterImpl extends EaseChatHistoryPresenter {

    @Override
    public void downloadCombineMessage(ChatMessage combinedMessage) {
        ChatClient.getInstance().chatManager().downloadAndParseCombineMessage(combinedMessage, new ValueCallBack<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> value) {
                runOnUI(()-> mView.downloadCombinedMessagesSuccess(value));
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUI(()-> mView.downloadCombinedMessagesFailed(error, errorMsg));
            }
        });
    }

    @Override
    public void downloadThumbnail(ChatMessage message, int position) {
        if(message == null) {
            runOnUI(()-> mView.downloadVoiceFailed(message, position, Error.MESSAGE_INVALID, "Message in position: " + position + " is null."));
            return;
        }
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                runOnUI(()-> mView.downloadThumbnailSuccess(message, position));
            }

            @Override
            public void onError(int code, String error) {
                runOnUI(()-> mView.downloadThumbnailFailed(message, position, code, error));
            }
        });
        ChatClient.getInstance().chatManager().downloadThumbnail(message);
    }

    @Override
    public void downloadVoice(ChatMessage message, int position) {
        if(message == null) {
            runOnUI(()-> mView.downloadVoiceFailed(message, position, Error.MESSAGE_INVALID, "Message in position: " + position + " is null."));
            return;
        }
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                runOnUI(()-> mView.downloadVoiceSuccess(message, position));
            }

            @Override
            public void onError(int code, String error) {
                runOnUI(()-> mView.downloadVoiceFailed(message, position, code, error));
            }
        });
        ChatClient.getInstance().chatManager().downloadAttachment(message);
    }
}
