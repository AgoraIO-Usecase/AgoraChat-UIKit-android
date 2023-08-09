package io.agora.chat.uikit.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.activities.EaseChatHistoryActivity;
import io.agora.chat.uikit.activities.EaseChatThreadActivity;
import io.agora.chat.uikit.activities.EaseChatThreadCreateActivity;
import io.agora.chat.uikit.activities.EaseImageGridActivity;
import io.agora.chat.uikit.activities.EaseShowBigImageActivity;
import io.agora.chat.uikit.activities.EaseShowLocalVideoActivity;
import io.agora.chat.uikit.activities.EaseShowNormalFileActivity;
import io.agora.chat.uikit.activities.EaseShowVideoActivity;
import io.agora.chat.uikit.constants.EaseConstant;
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
     * Jump to chat history activity
     * @param context
     * @param combinedMessage
     */
    public static void startToChatHistoryActivity(Context context, ChatMessage combinedMessage) {
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseChatHistoryActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE, combinedMessage);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseChatHistoryActivity.actionStart(context, combinedMessage);
    }

    /**
     * Jump to create chat thread activity
     * @param launcher
     * @param context
     */
    public static void startToImageGridActivity(ActivityResultLauncher<Intent> launcher, Context context) {
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseImageGridActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                try {
                    launcher.launch(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        Intent intent = new Intent(context, EaseImageGridActivity.class);
        launcher.launch(intent);
    }

    /**
     * Jump to download video activity
     * @param context
     * @param message
     */
    public static void startToDownloadVideoActivity(Context context, ChatMessage message) {
        if(context == null) {
            return;
        }
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseShowVideoActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("msg", message);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseShowVideoActivity.actionStart(context, message);
    }

    /**
     * Jump to local video activity
     * @param context
     * @param path
     */
    public static void startToLocalVideoActivity(Context context, String path) {
        if(context == null) {
            return;
        }
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseShowLocalVideoActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("path", path);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseShowLocalVideoActivity.actionStart(context, path);
    }

    /**
     * Jump to download file activity
     * @param context
     * @param message
     */
    public static void startToDownloadFileActivity(Context context, ChatMessage message) {
        if(context == null) {
            return;
        }
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseShowNormalFileActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("msg", message);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseShowNormalFileActivity.actionStart(context, message);
    }

    /**
     * Jump to local image activity
     * @param context
     * @param imageUri
     */
    public static void startToLocalImageActivity(Context context, Uri imageUri) {
        if(context == null) {
            return;
        }
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseShowBigImageActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("uri", imageUri);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseShowBigImageActivity.actionStart(context, imageUri);
    }

    /**
     * Jump to local image activity
     * @param context
     * @param messageId
     * @param filename
     */
    public static void startToLocalImageActivity(Context context, String messageId, String filename) {
        if(context == null) {
            return;
        }
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseShowBigImageActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("messageId", messageId);
                intent.putExtra("filename", filename);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseShowBigImageActivity.actionStart(context, messageId, filename);
    }

    /**
     * Jump to local image activity
     * @param context
     * @param message
     */
    public static void startToLocalImageActivity(Context context, ChatMessage message) {
        if(context == null) {
            return;
        }
        EaseActivityProvider provider = EaseUIKit.getInstance().getActivitiesProvider();
        if(provider != null) {
            Class activity = provider.getActivity(EaseShowBigImageActivity.class.getSimpleName());
            if(activity != null) {
                Intent intent = new Intent(context, activity);
                intent.putExtra("msg", message);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        EaseShowBigImageActivity.actionStart(context, message);
    }
}
