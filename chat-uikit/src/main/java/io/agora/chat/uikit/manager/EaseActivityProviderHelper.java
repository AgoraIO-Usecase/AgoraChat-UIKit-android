package io.agora.chat.uikit.manager;

import android.content.Context;
import android.content.Intent;

import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.activities.EaseChatThreadActivity;
import io.agora.chat.uikit.provider.EaseActivityProvider;

public class EaseActivityProviderHelper {
    /**
     * Skip to chat thread activity
     * @param context
     * @param conversationId
     * @param messageId
     * @param parentId
     */
    public static void startToChatThreadActivity(Context context, String conversationId, String messageId, String parentId) {
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseChatThreadActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("parentMsgId", messageId);
                intent.putExtra("conversationId", conversationId);
                intent.putExtra("parentId", parentId);
                context.startActivity(intent);
                return;
            }
        }
        EaseChatThreadActivity.actionStart(context, conversationId, messageId, parentId);
    }
}
