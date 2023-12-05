package io.agora.chat.uikit.manager;


import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.CombineMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseUserUtils;

/**
 * It is a helper that helps us operate multi-select chat messages.
 */
public class EaseChatMessageMultiSelectHelper {
    private volatile static EaseChatMessageMultiSelectHelper instance;
    private Map<Context, innerData> dataMap;

    private EaseChatMessageMultiSelectHelper() {
        dataMap = new HashMap<>();
    }

    public static EaseChatMessageMultiSelectHelper getInstance() {
        if(instance == null) {
            synchronized (EaseChatMessageMultiSelectHelper.class) {
                if(instance == null) {
                    instance = new EaseChatMessageMultiSelectHelper();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if(context == null) {
            return;
        }
        if(dataMap.containsKey(context)) {
            return;
        }
        dataMap.put(context, new innerData());
    }

    /**
     * Add message to selectedMap.
     * @param context
     * @param message
     */
    public void addChatMessage(Context context, ChatMessage message) {
        if(message == null || context == null) {
            return;
        }
        checkInnerData(context);
        dataMap.get(context).addChatMessage(message);
    }

    private void checkInnerData(Context context) {
        if(dataMap.containsKey(context)) {
            return;
        }
        init(context);
    }

    private boolean hasInnerData(Context context) {
        return dataMap.containsKey(context);
    }

    /**
     * Remove message from selectedMap.
     * @param context
     * @param message
     */
    public void removeChatMessage(Context context, ChatMessage message) {
        if(message == null || context == null) {
            return;
        }
        if(!hasInnerData(context)) {
            return;
        }
        dataMap.get(context).removeChatMessage(message);
    }

    /**
     * Determines whether the message is contained.
     *
     * @param context
     * @param message
     * @return
     */
    public boolean isContainsMessage(Context context, ChatMessage message) {
        if(message == null) {
            return false;
        }
        if(!hasInnerData(context)) {
            return false;
        }
        return dataMap.get(context).isContainsMessage(message);
    }

    /**
     * Get the sorted message id list.
     * @return
     * @param context
     */
    public List<String> getSortedMessages(Context context) {
        if(context == null || !hasInnerData(context)) {
            return new ArrayList<>();
        }
        return dataMap.get(context).getSortedMessages();
    }

    public static String getCombineMessageSummary(List<String> messageList) {
        if(messageList.isEmpty()) {
            return "";
        }
        List<String> subMessageList;
        if(messageList.size() > 3) {
            subMessageList = messageList.subList(0, 3);
        }else {
            subMessageList = messageList;
        }
        StringBuilder summary = new StringBuilder();
        String simpleName = "";
        for(int i = 0; i < subMessageList.size(); i++) {
            String msgId = subMessageList.get(i);
            ChatMessage message = ChatClient.getInstance().chatManager().getMessage(msgId);
            ChatMessage.Type type = message.getType();
            simpleName = message.getBody().getClass().getSimpleName();
            EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
            summary.append(user == null ? message.getFrom() : user.getNickname()).append(": ");
            switch (type) {
                case TXT :
                    summary.append(((TextMessageBody)message.getBody()).getMessage());
                    break;
                case COMBINE :
                    summary.append(((CombineMessageBody)message.getBody()).getTitle());
                    break;
                default:
                    summary.append("/").append(simpleName.substring(0, simpleName.length() - 4)).append("/");
                    break;
            }
            if(i < subMessageList.size() - 1) {
                summary.append("\n");
            }
        }
        return summary.toString();
    }

    /**
     * Set is multi style.
     * @param context
     * @param isMultiStyle
     */
    public void setMultiStyle(Context context, boolean isMultiStyle) {
        if(context == null || !hasInnerData(context)) {
            return;
        }
        dataMap.get(context).setMultiStyle(isMultiStyle);
    }

    /**
     * Get the multi style.
     * @return
     * @param context
     */
    public boolean isMultiStyle(Context context) {
        if(context == null || !hasInnerData(context)) {
            return false;
        }
        return dataMap.get(context).isMultiStyle();
    }

    /**
     * Clear the selectedMap.
     * @param context
     */
    public void clear(Context context) {
        if(context == null || !hasInnerData(context)) {
            return;
        }
        dataMap.get(context).clear();
    }

    private static class innerData {
        private Map<Long, String> selectedMap;
        private Set<String> toSendUserIds;
        private boolean isMultiStyle;

        public innerData() {
            selectedMap = new HashMap<>();
            toSendUserIds = new HashSet<>();
        }

        /**
         * Add message to selectedMap.
         * @param message
         */
        public void addChatMessage(ChatMessage message) {
            if(message == null) {
                return;
            }
            selectedMap.put(message.getMsgTime(), message.getMsgId());
        }

        /**
         * Remove message from selectedMap.
         * @param message
         */
        public void removeChatMessage(ChatMessage message) {
            if(message == null) {
                return;
            }
            selectedMap.remove(message.getMsgTime());
        }

        /**
         * Determines whether the message is contained.
         * @param message
         * @return
         */
        public boolean isContainsMessage(ChatMessage message) {
            if(message == null) {
                return false;
            }
            return selectedMap.containsKey(message.getMsgTime());
        }

        /**
         * Get the sorted message id list.
         * @return
         */
        public List<String> getSortedMessages() {
            if(selectedMap.isEmpty()) {
                return new ArrayList<>();
            }
            List<Long> timeList = new ArrayList<>(selectedMap.keySet());
            Collections.sort(timeList, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    if(o2 - o1 > 0) {
                        return -1;
                    }else if(o2.equals(o1)) {
                        return 0;
                    }else {
                        return 1;
                    }
                }
            });
            List<String> msgIdList = new ArrayList<>();
            for(Long timestamp : timeList) {
                msgIdList.add(selectedMap.get(timestamp));
            }
            return msgIdList;
        }

        public static String getCombineMessageSummary(List<String> messageList) {
            if(messageList.isEmpty()) {
                return "";
            }
            List<String> subMessageList;
            if(messageList.size() > 3) {
                subMessageList = messageList.subList(0, 3);
            }else {
                subMessageList = messageList;
            }
            StringBuilder summary = new StringBuilder();
            String simpleName = "";
            for(int i = 0; i < subMessageList.size(); i++) {
                String msgId = subMessageList.get(i);
                ChatMessage message = ChatClient.getInstance().chatManager().getMessage(msgId);
                ChatMessage.Type type = message.getType();
                simpleName = message.getBody().getClass().getSimpleName();
                EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
                summary.append(user == null ? message.getFrom() : user.getNickname()).append(": ");
                switch (type) {
                    case TXT :
                        summary.append(((TextMessageBody)message.getBody()).getMessage());
                        break;
                    case COMBINE :
                        summary.append(((CombineMessageBody)message.getBody()).getTitle());
                        break;
                    default:
                        summary.append("/").append(simpleName.substring(0, simpleName.length() - 4)).append("/");
                        break;
                }
                if(i < subMessageList.size() - 1) {
                    summary.append("\n");
                }
            }
            return summary.toString();
        }

        /**
         * Set is multi style.
         * @param isMultiStyle
         */
        public void setMultiStyle(boolean isMultiStyle) {
            this.isMultiStyle = isMultiStyle;
        }

        /**
         * Get the multi style.
         * @return
         */
        public boolean isMultiStyle() {
            return isMultiStyle;
        }

        /**
         * Clear the selectedMap.
         */
        public void clear() {
            selectedMap.clear();
            toSendUserIds.clear();
            isMultiStyle = false;
        }
    }
}
