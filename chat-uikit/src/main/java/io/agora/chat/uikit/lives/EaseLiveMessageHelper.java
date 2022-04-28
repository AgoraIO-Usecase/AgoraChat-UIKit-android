package io.agora.chat.uikit.lives;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.CallBack;
import io.agora.MessageListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.CustomMessageBody;
import io.agora.chat.MessageBody;
import io.agora.util.EMLog;

public class EaseLiveMessageHelper {
    private final static String TAG = EaseLiveMessageHelper.class.getSimpleName();
    private static EaseLiveMessageHelper instance;
    private final LiveMessageListener messageListener;
    private static String chatroomId;


    private EaseLiveMessageHelper() {
        messageListener = new LiveMessageListener();
    }

    public static EaseLiveMessageHelper getInstance() {
        if (instance == null) {
            synchronized (EaseLiveMessageHelper.class) {
                if (instance == null) {
                    instance = new EaseLiveMessageHelper();
                }
            }
        }
        return instance;
    }

    public void init(String chatRoomId) {
        this.chatroomId = chatRoomId;
        messageListener.setChatRoomId(chatRoomId);
        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public void removeMessageListener() {
        ChatClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    public void setLiveMessageListener(OnLiveMessageListener liveMessageListener) {
        messageListener.setLiveMessageListener(liveMessageListener);
    }

    /**
     * Send a text message
     *
     * @param content
     * @param callBack
     */
    public void sendTxtMsg(String content, OnSendLiveMessageCallBack callBack) {
        ChatMessage message = ChatMessage.createTxtSendMessage(content, chatroomId);
        message.setChatType(ChatMessage.ChatType.ChatRoom);
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                callBack.onSuccess();
                callBack.onSuccess(message);
            }

            @Override
            public void onError(int i, String s) {
                callBack.onError(i, s);
                callBack.onError(message.getMsgId(), i, s);
            }

            @Override
            public void onProgress(int i, String s) {
                callBack.onProgress(i, s);
            }
        });
        ChatClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * send gift message
     *
     * @param giftId
     * @param num
     * @param callBack
     */
    public void sendGiftMsg(String chatRoomId, String giftId, int num, OnSendLiveMessageCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID, giftId);
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM, String.valueOf(num));
        sendGiftMsg(chatRoomId, params, callBack);
    }

    /**
     * send gift message
     *
     * @param params
     * @param callBack
     */
    public void sendGiftMsg(String chatRoomId, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        if (params.size() <= 0) {
            return;
        }
        sendCustomMsg(chatRoomId, EaseLiveMessageType.CHATROOM_GIFT.getName(), params, callBack);
    }

    /**
     * send praise message
     *
     * @param num
     * @param callBack
     */
    public void sendPraiseMsg(String chatRoomId, int num, OnSendLiveMessageCallBack callBack) {
        if (num <= 0) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM, String.valueOf(num));
        sendPraiseMsg(chatRoomId, params, callBack);
    }

    /**
     * send praise message
     *
     * @param params
     * @param callBack
     */
    public void sendPraiseMsg(String chatRoomId, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        if (params.size() <= 0) {
            return;
        }
        sendCustomMsg(chatRoomId, EaseLiveMessageType.CHATROOM_PRAISE.getName(), params, callBack);
    }

    /**
     * send barrage message
     *
     * @param content
     * @param callBack
     */
    public void sendBarrageMsg(String chatRoomId, String content, final OnSendLiveMessageCallBack callBack) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(EaseLiveMessageConstant.LIVE_MESSAGE_BARRAGE_KEY_TXT, content);
        sendBarrageMsg(chatRoomId, params, callBack);
    }

    /**
     * send barrage message
     *
     * @param params
     * @param callBack
     */
    public void sendBarrageMsg(String chatRoomId, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        if (params.size() <= 0) {
            return;
        }
        sendCustomMsg(chatRoomId, EaseLiveMessageType.CHATROOM_BARRAGE.getName(), params, callBack);
    }

    /**
     * send custom message
     *
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String chatRoomId, String event, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        sendCustomMsg(chatRoomId, ChatMessage.ChatType.ChatRoom, event, params, callBack);
    }

    /**
     * send custom message
     *
     * @param to
     * @param chatType
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String to, ChatMessage.ChatType chatType, String event, Map<String, String> params, final OnSendLiveMessageCallBack callBack) {
        final ChatMessage sendMessage = ChatMessage.createSendMessage(ChatMessage.Type.CUSTOM);
        CustomMessageBody body = new CustomMessageBody(event);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(to);
        sendMessage.setChatType(chatType);
        sendMessage.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                if (callBack != null) {
                    callBack.onSuccess(sendMessage);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (callBack != null) {
                    callBack.onError(sendMessage.getMsgId(), i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {
                if (callBack != null) {
                    callBack.onProgress(i, s);
                }
            }
        });
        ChatClient.getInstance().chatManager().sendMessage(sendMessage);
    }

    /**
     * get gift id from message
     *
     * @param msg
     * @return
     */
    public String getMsgGiftId(ChatMessage msg) {
        if (!isGiftMsg(msg)) {
            return null;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID)) {
            return params.get(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_ID);
        }
        return null;
    }

    /**
     * get the number of gift in message
     *
     * @param msg
     * @return
     */
    public int getMsgGiftNum(ChatMessage msg) {
        if (!isGiftMsg(msg)) {
            return 0;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM)) {
            String num = params.get(EaseLiveMessageConstant.LIVE_MESSAGE_GIFT_KEY_NUM);
            if (TextUtils.isEmpty(num)) {
                return 0;
            }
            try {
                return Integer.parseInt(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * get the number of praise in message
     *
     * @param msg
     * @return
     */
    public int getMsgPraiseNum(ChatMessage msg) {
        if (!isPraiseMsg(msg)) {
            return 0;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM)) {
            String num = params.get(EaseLiveMessageConstant.LIVE_MESSAGE_PRAISE_KEY_NUM);
            if (TextUtils.isEmpty(num)) {
                return 0;
            }
            try {
                return Integer.parseInt(num);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * get the content of barrage in message
     *
     * @param msg
     * @return
     */
    public String getMsgBarrageTxt(ChatMessage msg) {
        if (!isBarrageMsg(msg)) {
            return null;
        }
        Map<String, String> params = getCustomMsgParams(msg);
        if (params.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_BARRAGE_KEY_TXT)) {
            return params.get(EaseLiveMessageConstant.LIVE_MESSAGE_BARRAGE_KEY_TXT);
        }
        return null;
    }

    /**
     * whether is gift message
     *
     * @param msg
     * @return
     */
    public boolean isGiftMsg(ChatMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EaseLiveMessageType.CHATROOM_GIFT;
    }

    /**
     * whether is praise message
     *
     * @param msg
     * @return
     */
    public boolean isPraiseMsg(ChatMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EaseLiveMessageType.CHATROOM_PRAISE;
    }

    /**
     * whether is barrage message
     *
     * @param msg
     * @return
     */
    public boolean isBarrageMsg(ChatMessage msg) {
        return getCustomMsgType(getCustomEvent(msg)) == EaseLiveMessageType.CHATROOM_BARRAGE;
    }

    /**
     * get the event of custom message
     *
     * @param message
     * @return
     */
    public String getCustomEvent(ChatMessage message) {
        if (message == null) {
            return null;
        }
        if (!(message.getBody() instanceof CustomMessageBody)) {
            return null;
        }
        return ((CustomMessageBody) message.getBody()).event();
    }

    /**
     * get the params of message
     *
     * @param message
     * @return
     */
    public Map<String, String> getCustomMsgParams(ChatMessage message) {
        if (message == null) {
            return null;
        }
        MessageBody body = message.getBody();
        if (!(body instanceof CustomMessageBody)) {
            return null;
        }
        return ((CustomMessageBody) body).getParams();
    }

    /**
     * get the type of custom message
     *
     * @param event
     * @return
     */
    public EaseLiveMessageType getCustomMsgType(String event) {
        if (TextUtils.isEmpty(event)) {
            return null;
        }
        return EaseLiveMessageType.fromName(event);
    }

    static class LiveMessageListener implements MessageListener {
        private String chatRoomId;
        private OnLiveMessageListener liveMessageListener;


        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;

        }

        public void setLiveMessageListener(OnLiveMessageListener liveMessageListener) {
            this.liveMessageListener = liveMessageListener;
        }

        @Override
        public void onMessageReceived(List<ChatMessage> messages) {
            EMLog.i(TAG, "onMessageReceived messages size=" + messages.size());
            for (ChatMessage message : messages) {
                if (message.getType() != ChatMessage.Type.CUSTOM) {
                    continue;
                }

                if (message.getChatType() != ChatMessage.ChatType.GroupChat && message.getChatType() != ChatMessage.ChatType.ChatRoom) {
                    continue;
                }
                String username = message.getTo();

                if (!TextUtils.equals(username, chatRoomId)) {
                    continue;
                }

                CustomMessageBody body = (CustomMessageBody) message.getBody();
                String event = body.event();

                if (TextUtils.isEmpty(event)) {
                    continue;
                }
                EaseLiveMessageType msgType = EaseLiveMessageHelper.getInstance().getCustomMsgType(event);
                if (msgType == null) {
                    continue;
                }

                switch (msgType) {
                    case CHATROOM_GIFT:
                        if (null != liveMessageListener) {
                            liveMessageListener.onGiftMessageReceived(message);
                        }
                        break;
                    case CHATROOM_PRAISE:
                        if (null != liveMessageListener) {
                            liveMessageListener.onPraiseMessageReceived(message);
                        }
                        break;
                    case CHATROOM_BARRAGE:
                        if (null != liveMessageListener) {
                            liveMessageListener.onBarrageMessageReceived(message);
                        }
                        break;
                }
            }

            if (null != liveMessageListener) {
                liveMessageListener.onMessageReceived(messages);
            }
        }

        @Override
        public void onCmdMessageReceived(List<ChatMessage> messages) {
            ChatMessage message = messages.get(messages.size() - 1);
//            if (DemoConstants.CMD_GIFT.equals(((CmdMessageBody) message.getBody()).action())) {
//                //showLeftGiftView(message.getFrom());
//            } else if (DemoConstants.CMD_PRAISE.equals(((CmdMessageBody) message.getBody()).action())) {
//                if (onChatRoomListener != null) {
//                    // onChatRoomListener.onReceivePraiseMsg(message.getIntAttribute(DemoConstants.EXTRA_PRAISE_COUNT, 1));
//                }
//            }
        }

        @Override
        public void onMessageRecalled(List<ChatMessage> list) {

        }

        @Override
        public void onMessageChanged(ChatMessage chatMessage, Object o) {
            if (null != liveMessageListener) {
                liveMessageListener.onMessageChanged();
            }
        }
    }
}
