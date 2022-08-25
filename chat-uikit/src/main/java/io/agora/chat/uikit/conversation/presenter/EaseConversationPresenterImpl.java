package io.agora.chat.uikit.conversation.presenter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.Conversation;
import io.agora.chat.PushManager;
import io.agora.chat.SilentModeResult;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.manager.EaseNotificationMsgManager;
import io.agora.chat.uikit.manager.EasePreferenceManager;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.exceptions.ChatException;
import io.agora.util.EMLog;

public class EaseConversationPresenterImpl extends EaseConversationPresenter {

    private final List<Conversation> list = new ArrayList<>();
    /**
     * Note：The default setting extField values for the timestamp in the conversation, is the conversation placed at the top
     * If you have different logic, implement it yourself and call {@link #sortData(List)}
     */
    @Override
    public void loadData(boolean fetchConfig) {
        EMLog.e("holder: ","presenter load: " + fetchConfig);
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
                Map<String,Long> mute =  EasePreferenceManager.getInstance().getMuteMap();
                for (Conversation conversation : conversations.values()) {
                    if(conversation.getAllMessages().size() != 0) {
                        // Remove notification conversation
                        if(EaseNotificationMsgManager.getInstance().isNotificationConversation(conversation)) {
                            continue;
                        }
                        info = new EaseConversationInfo();
                        info.setInfo(conversation);
                        list.add(conversation);
                        if (mute.size() > 0){
                            for (Map.Entry<String, Long> entry : mute.entrySet()) {
                                if (conversation.conversationId().equals(entry.getKey())){
                                    EMLog.e("holder: ","pre id: " + conversation.conversationId() + " value: " + entry.getValue() + " currentTime: "+System.currentTimeMillis()
                                    + "  "+ ((System.currentTimeMillis() - entry.getValue()) < 0));
                                    info.setMute((System.currentTimeMillis() - entry.getValue()) < 0);
                                }
                            }
                        }
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
                    if (list.size() > 0){
                        pushManager.getSilentModeForConversations(list, new ValueCallBack<Map<String, SilentModeResult>>() {
                            @Override
                            public void onSuccess(Map<String, SilentModeResult> value) {
                                Map<String,Long> mute = new HashMap<>();
                                for (Map.Entry<String, SilentModeResult> resultEntry : value.entrySet()) {
                                    mute.put(resultEntry.getKey(), resultEntry.getValue().getExpireTimestamp());
                                }
                                if (mute.size() > 0){
                                    EasePreferenceManager.getInstance().setMuteMap(mute);
                                    if(isActive()) {
                                        runOnUI(()-> mView.loadMuteDataSuccess(infos));
                                    }
                                }
                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                    EMLog.e("pushManager","code: " + error + " - " + errorMsg);
                            }
                        });

                    }
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

