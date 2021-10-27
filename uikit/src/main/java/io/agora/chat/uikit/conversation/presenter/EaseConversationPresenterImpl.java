package io.agora.chat.uikit.conversation.presenter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.agora.chat.ChatClient;
import io.agora.chat.Conversation;
import io.agora.chat.PushManager;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.manager.EaseNotificationMsgManager;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.exceptions.ChatException;

public class EaseConversationPresenterImpl extends EaseConversationPresenter {

    /**
     * Note：The default setting extField values for the timestamp in the conversation, is the conversation placed at the top
     * If you have different logic, implement it yourself and call {@link #sortData(List)}
     */
    @Override
    public void loadData(boolean fetchConfig) {
        // get all conversations
        runOnIO(()-> {
            Map<String, Conversation> conversations = ChatClient.getInstance().chatManager().getAllConversations();
            if(conversations.isEmpty()) {
                runOnUI(() -> {
                    if(!isDestroy()) {
                        mView.loadConversationListNoData();
                    }
                });
                return;
            }
            List<EaseConversationInfo> infos = new ArrayList<>();
            synchronized (this) {
                EaseConversationInfo info = null;
                for (Conversation conversation : conversations.values()) {
                    if(conversation.getAllMessages().size() != 0) {
                        //如果不展示系统消息，则移除相关系统消息
                        if(!showSystemMessage) {
                            if(EaseNotificationMsgManager.getInstance().isNotificationConversation(conversation)) {
                                continue;
                            }
                        }
                        info = new EaseConversationInfo();
                        info.setInfo(conversation);
                        String extField = conversation.getExtField();
                        long lastMsgTime=conversation.getLastMessage().getMsgTime();
                        if(!TextUtils.isEmpty(extField) && EaseUtils.isTimestamp(extField)) {
                            info.setTop(true);
                            long makeTopTime=Long.parseLong(extField);
                            if(makeTopTime>lastMsgTime) {
                                info.setTimestamp(makeTopTime);
                            }else{
                                info.setTimestamp(lastMsgTime);
                            }
                        }else{
                            info.setTimestamp(lastMsgTime);
                        }
                        infos.add(info);
                    }
                }
            }
            if(isActive()) {
                runOnUI(()-> mView.loadConversationListSuccess(infos));
            }
            // Get the no push groups
            PushManager pushManager = ChatClient.getInstance().pushManager();
            try {
                // Should update from server first.
                if(fetchConfig) {
                    pushManager.getPushConfigsFromServer();
                }
                List<String> noPushGroups = pushManager.getNoPushGroups();
                List<String> noPushUsers = pushManager.getNoPushUsers();
                if((noPushGroups == null || noPushGroups.size() <= 0) && (noPushUsers == null || noPushUsers.size() <= 0)) {
                    return;
                }
                for (EaseConversationInfo info : infos){
                    info.setMute(false);
                    Object item = info.getInfo();
                    if(item instanceof Conversation) {
                        if((noPushGroups != null && noPushGroups.contains(((Conversation) item).conversationId()))
                                || (noPushUsers != null && noPushUsers.contains(((Conversation) item).conversationId())) ) {
                            info.setMute(true);
                        }
                    }
                }

                if(isActive()) {
                    runOnUI(()-> mView.loadMuteDataSuccess(infos));
                }

            } catch (ChatException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Sort conversation info by timestamp
     * @param data
     */
    @Override
    public void sortData(List<EaseConversationInfo> data) {
        if(data == null || data.isEmpty()) {
            runOnUI(() -> {
                if(!isDestroy()) {
                    mView.loadConversationListNoData();
                }

            });
            return;
        }
        List<EaseConversationInfo> sortList = new ArrayList<>();
        List<EaseConversationInfo> topSortList = new ArrayList<>();
        synchronized (this) {
            for(EaseConversationInfo info : data) {
                if(info.isTop()) {
                    topSortList.add(info);
                }else {
                    sortList.add(info);
                }
            }
            sortByTimestamp(topSortList);
            sortByTimestamp(sortList);
            sortList.addAll(0, topSortList);
        }
        runOnUI(() -> {
            if(!isDestroy()) {
                mView.sortConversationListSuccess(sortList);
            }
        });
    }

    /**
     * Sort
     * @param list
     */
    private void sortByTimestamp(List<EaseConversationInfo> list) {
        if(list == null || list.isEmpty()) {
            return;
        }
        Collections.sort(list, new Comparator<EaseConversationInfo>() {
            @Override
            public int compare(EaseConversationInfo o1, EaseConversationInfo o2) {
                if(o2.getTimestamp() > o1.getTimestamp()) {
                    return 1;
                }else if(o2.getTimestamp() == o1.getTimestamp()) {
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

    @Override
    public void makeConversionRead(int position, EaseConversationInfo info) {
        if(info.getInfo() instanceof Conversation) {
            ((Conversation) info.getInfo()).markAllMessagesAsRead();
        }
        if(!isDestroy()) {
            mView.refreshList(position);
        }
    }

    @Override
    public void makeConversationTop(int position, EaseConversationInfo info) {
        if(info.getInfo() instanceof Conversation) {
            long timestamp = System.currentTimeMillis();
            ((Conversation) info.getInfo()).setExtField(timestamp +"");
            info.setTop(true);
            info.setTimestamp(timestamp);
        }
        if(!isDestroy()) {
            mView.refreshList();
        }
    }

    @Override
    public void cancelConversationTop(int position, EaseConversationInfo info) {
        if(info.getInfo() instanceof Conversation) {
            ((Conversation) info.getInfo()).setExtField("");
            info.setTop(false);
            info.setTimestamp(((Conversation) info.getInfo()).getLastMessage().getMsgTime());
        }
        if(!isDestroy()) {
            mView.refreshList();
        }
    }

    @Override
    public void deleteConversation(int position, EaseConversationInfo info) {
        if(info.getInfo() instanceof Conversation) {
            boolean isDelete = ChatClient.getInstance().chatManager()
                                .deleteConversation(((Conversation) info.getInfo()).conversationId()
                                        , !TextUtils.equals(((Conversation) info.getInfo()).conversationId(), EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID));
            if(!isDestroy()) {
                if(isDelete) {
                    mView.deleteItem(position);
                }else {
                    mView.deleteItemFail(position, "");
                }
            }

        }
    }
}

