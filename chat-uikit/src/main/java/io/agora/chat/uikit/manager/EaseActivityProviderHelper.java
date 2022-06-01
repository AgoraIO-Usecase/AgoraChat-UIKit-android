package io.agora.chat.uikit.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.activities.EaseChatThreadActivity;
import io.agora.chat.uikit.activities.EaseChatThreadCreateActivity;
import io.agora.chat.uikit.activities.EaseImageGridActivity;
import io.agora.chat.uikit.provider.EaseActivityProvider;

public class EaseActivityProviderHelper {
    /**
     * Jump to chat thread activity
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
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseChatThreadActivity.actionStart(context, conversationId, messageId, parentId);
    }

    /**
     * Jump to create chat thread activity
     * @param context
     * @param parentId Group Id
     * @param messageId Group's message ID
     */
    public static void startToCreateChatThreadActivity(Context context, String parentId, String messageId) {
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseChatThreadCreateActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("messageId", messageId);
                intent.putExtra("parentId", parentId);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseChatThreadCreateActivity.actionStart(context, parentId, messageId);
    }

    /**
     * Jump to create chat thread activity
     * @param context
     * @param requestCode
     */
    public static void startToImageGridActivity(Activity context, int requestCode) {
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseImageGridActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                try {
                    context.startActivityForResult(intent, requestCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        Intent intent = new Intent(context, EaseImageGridActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * Jump to create chat thread activity
     * @param context
     * @param requestCode
     */
    public static void startToImageGridActivity(Fragment context, int requestCode) {
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseImageGridActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context.getContext(), activity);
                try {
                    context.startActivityForResult(intent, requestCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        Intent intent = new Intent(context.getContext(), EaseImageGridActivity.class);
        context.startActivityForResult(intent, requestCode);
    }
}
