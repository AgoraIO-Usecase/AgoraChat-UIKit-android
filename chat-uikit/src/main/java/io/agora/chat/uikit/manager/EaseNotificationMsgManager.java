package io.agora.chat.uikit.manager;

import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.constants.EaseConstant;

public class EaseNotificationMsgManager {
    private static EaseNotificationMsgManager instance;

    private EaseNotificationMsgManager(){}

    public static EaseNotificationMsgManager getInstance() {
        if(instance == null) {
            synchronized (EaseNotificationMsgManager.class) {
                if(instance == null) {
                    instance = new EaseNotificationMsgManager();
                }
            }
        }
        return instance;
    }

    /**
     * Create notification message
     * @param message
     * @param ext
     * @return
     */
    public ChatMessage createMessage(String message, Map<String, Object> ext) {
        ChatMessage emMessage = ChatMessage.createReceiveMessage(ChatMessage.Type.TXT);
        emMessage.setFrom(EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID);
        emMessage.setMsgId(UUID.randomUUID().toString());
        emMessage.setStatus(ChatMessage.Status.SUCCESS);
        emMessage.addBody(new TextMessageBody(message));
        if(ext != null && !ext.isEmpty()) {
            Iterator<String> iterator = ext.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = ext.get(key);
                putObject(emMessage, key, value);
            }
        }
        emMessage.setUnread(true);
        ChatClient.getInstance().chatManager().saveMessage(emMessage);
        return emMessage;
    }

    private void putObject(ChatMessage message, String key, Object value) {
        if(TextUtils.isEmpty(key)) {
            return;
        }
        if(value instanceof String) {
            message.setAttribute(key, (String) value);
        }else if(value instanceof Byte) {
            message.setAttribute(key, (Integer) value);
        }else if(value instanceof Character) {
            message.setAttribute(key, (Integer) value);
        }else if(value instanceof Short) {
            message.setAttribute(key, (Integer) value);
        }else if(value instanceof Integer) {
            message.setAttribute(key, (Integer) value);
        }else if(value instanceof Boolean) {
            message.setAttribute(key, (Boolean) value);
        }else if(value instanceof Long) {
            message.setAttribute(key, (Long) value);
        }else if(value instanceof Float) {
            JSONObject object = new JSONObject();
            try {
                object.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            message.setAttribute(key, object);
        }else if(value instanceof Double) {
            JSONObject object = new JSONObject();
            try {
                object.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            message.setAttribute(key, object);
        }else if(value instanceof JSONObject) {
            message.setAttribute(key, (JSONObject) value);
        }else if(value instanceof JSONArray) {
            message.setAttribute(key, (JSONArray) value);
        }else {
            JSONObject object = new JSONObject();
            try {
                object.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            message.setAttribute(key, object);
        }
    }

    /**
     * Create ext map
     * @return
     */
    public Map<String, Object> createMsgExt() {
        return new HashMap<>();
    }

    /**
     * Get latest message
     * @param con
     * @return
     */
    public ChatMessage getLastMessageByConversation(Conversation con) {
        if(con == null) {
            return null;
        }
        return con.getLastMessage();
    }

    /**
     * Get notification conversation
     * @return
     */
    public Conversation getConversation() {
        return getConversation(true);
    }

    /**
     * Get notification conversation
     * @param createIfNotExists
     * @return
     */
    public Conversation getConversation(boolean createIfNotExists) {
        return ChatClient.getInstance().chatManager().getConversation(EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID
                , Conversation.ConversationType.Chat, createIfNotExists);
    }

    /**
     * Get all messages of notification
     * @return
     */
    public List<ChatMessage> getAllMessages() {
        return getConversation().getAllMessages();
    }

    /**
     * Check whether is a notification message
     * @param message
     * @return
     */
    public boolean isNotificationMessage(ChatMessage message) {
        return message.getType() == ChatMessage.Type.TXT
                && TextUtils.equals(message.getFrom(), EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID);
    }

    /**
     * Check whether is a notification conversation
     * @param conversation
     * @return
     */
    public boolean isNotificationConversation(Conversation conversation) {
        return conversation.getType() == Conversation.ConversationType.Chat
                && TextUtils.equals(conversation.conversationId(), EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID);
    }

    /**
     * Get the message content
     * @param message
     * @return
     */
    public String getMessageContent(ChatMessage message) {
        if(message.getBody() instanceof TextMessageBody) {
            return ((TextMessageBody)message.getBody()).getMessage();
        }
        return "";
    }

    /**
     * Update notification message
     * @param message
     * @return
     */
    public boolean updateMessage(ChatMessage message) {
        if(message == null || !isNotificationMessage(message)) {
            return false;
        }
        ChatClient.getInstance().chatManager().updateMessage(message);
        return true;
    }

    /**
     * Remove notification message
     * @param message
     * @return
     */
    public boolean removeMessage(ChatMessage message) {
        if(message == null || !isNotificationMessage(message)) {
            return false;
        }
        Conversation conversation = ChatClient.getInstance().chatManager().getConversation(EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID);
        conversation.removeMessage(message.getMsgId());
        return true;
    }

    /**
     * Make all message in notification conversation as read
     */
    public void markAllMessagesAsRead() {
        Conversation conversation = ChatClient.getInstance().chatManager().getConversation(EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID);
        conversation.markAllMessagesAsRead();
    }
}

