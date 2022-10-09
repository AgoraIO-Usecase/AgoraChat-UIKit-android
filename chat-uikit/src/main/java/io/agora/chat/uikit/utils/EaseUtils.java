/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agora.chat.uikit.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import androidx.annotation.BoolRes;
import androidx.annotation.IdRes;

import java.util.ArrayList;
import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;
import io.agora.util.EMLog;

public class EaseUtils {
	private static final String TAG = "CommonUtils";
	/**
	 * check if network avalable
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
			}
		}

		return false;
	}

	/**
	 * check if sdcard exist
	 * 
	 * @return
	 */
	public static boolean isSdcardExist() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	public static ChatMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode){
        ChatMessage message = ChatMessage.createTxtSendMessage("["+expressioName+"]", toChatUsername);
        if(identityCode != null){
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
        }
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
        return message;
	}

	/**
     * Get digest according message type and content
     * 
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(ChatMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
        case LOCATION:
            if (message.direct() == ChatMessage.Direct.RECEIVE) {
                digest = getString(context, R.string.ease_location_recv);
                EaseUserProfileProvider userProvider = EaseUIKit.getInstance().getUserProvider();
                String from = message.getFrom();
                if(userProvider != null && userProvider.getUser(from) != null) {
                    EaseUser user = userProvider.getUser(from);
                    if(user != null && !TextUtils.isEmpty(user.getNickname())) {
                        from = user.getNickname();
                    }
                }
                digest = String.format(digest, from);
                return digest;
            } else {
                digest = getString(context, R.string.ease_location_prefix);
            }
            break;
        case IMAGE:
            digest = getString(context, R.string.ease_picture);
            break;
        case VOICE:
            digest = getString(context, R.string.ease_voice_prefix);
            break;
        case VIDEO:
            digest = getString(context, R.string.ease_video);
            break;
        case CUSTOM:
            digest = getString(context, R.string.ease_custom);
            break;
        case TXT:
            TextMessageBody txtBody = (TextMessageBody) message.getBody();
            if(txtBody != null){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    if(!TextUtils.isEmpty(txtBody.getMessage())){
                        digest = txtBody.getMessage();
                    }else{
                        digest = getString(context, R.string.ease_dynamic_expression);
                    }
                }else{
                    digest = txtBody.getMessage();
                }
            }
            break;
        case FILE:
            digest = getString(context, R.string.ease_file);
            break;
        default:
            EMLog.e(TAG, "error, unknow type");
            return "";
        }
//        EMLog.e("TAG", "message text = "+digest);
        return digest;
    }
    
    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }
	
	/**
	 * get top context
	 * @param context
	 * @return
	 */
	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}
	
	/**
     * set initial letter of according user's nickname( username if no nickname)
     * 
     * @param user
     */
    public static void setUserInitialLetter(EaseUser user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter {
            String getLetter(String name) {
                if (TextUtils.isEmpty(name)) {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0)) {
                    return DefaultLetter;
                }
                String pinyin = HanziToPinyin.getPinyin(name);
                if(!TextUtils.isEmpty(pinyin)) {
                    String letter = pinyin.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if(c < 'A' || c > 'Z') {
                        return DefaultLetter;
                    }
                    return letter;
                }
                return DefaultLetter;
            }
        }

        if ( !TextUtils.isEmpty(user.getNickname()) ) {
            letter = new GetInitialLetter().getLetter(user.getNickname());
            user.setInitialLetter(letter);
            return;
        }
        if (letter.equals(DefaultLetter) && !TextUtils.isEmpty(user.getUsername())) {
            letter = new GetInitialLetter().getLetter(user.getUsername());
        }
        user.setInitialLetter(letter);
    }
    
    /**
     * change the chat type to EMConversationType
     * @param chatType
     * @return
     */
    public static Conversation.ConversationType getConversationType(int chatType) {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            return Conversation.ConversationType.Chat;
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            return Conversation.ConversationType.GroupChat;
        } else {
            return Conversation.ConversationType.ChatRoom;
        }
    }

    /**
     * change the chat type to EMConversationType
     * @param chatType
     * @return
     */
    public static Conversation.ConversationType getConversationType(EaseChatType chatType) {
        if (chatType == EaseChatType.SINGLE_CHAT) {
            return Conversation.ConversationType.Chat;
        } else if (chatType == EaseChatType.GROUP_CHAT) {
            return Conversation.ConversationType.GroupChat;
        } else {
            return Conversation.ConversationType.ChatRoom;
        }
    }

    /**
     * get chat type by conversation type
     * @param conversation
     * @return
     */
    public static EaseChatType getChatType(Conversation conversation) {
        if(conversation.isGroup()) {
            if(conversation.getType() == Conversation.ConversationType.ChatRoom) {
                return EaseChatType.CHATROOM;
            }else {
                return EaseChatType.GROUP_CHAT;
            }
        }else {
            return EaseChatType.SINGLE_CHAT;
        }
    }

    /**
     * \~chinese
     * Determine whether it is a do not disturb message, if it is in the app, it should not prompt the user for a new message
     * @param message
     * return
     *
     * \~english
     * check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     *
     * @param message
     * @return
     */
    public static boolean isSilentMessage(ChatMessage message){
        return message.getBooleanAttribute("em_ignore_notification", false);
    }

    /**
     * Get basic information of the screen
     * @param context
     * @return
     */
    public static float[] getScreenInfo(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        float[] info = new float[5];
        if(manager != null) {
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            info[0] = dm.widthPixels;
            info[1] = dm.heightPixels;
            info[2] = dm.densityDpi;
            info[3] = dm.density;
            info[4] = dm.scaledDensity;
        }
        return info;
    }

    /**
     * dip to px
     * @param context
     * @param value
     * @return
     */
    public static float dip2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * sp to px
     * @param context
     * @param value
     * @return
     */
    public static float sp2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * Determine whether it is a timestamp
     * @param time
     * @return
     */
    public static boolean isTimestamp(String time) {
        if(TextUtils.isEmpty(time)) {
            return false;
        }
        long timestamp = 0L;
        try {
            timestamp = Long.parseLong(time);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return timestamp > 0;
    }

    /**
     * Get initials
     * @param name
     * @return
     */
    public static String getLetter(String name) {
        return new GetInitialLetter().getLetter(name);
    }

    private static class GetInitialLetter {
        private String defaultLetter = "#";

        /**
         * Get initials
         * @param name
         * @return
         */
        public String getLetter(String name) {
            if(TextUtils.isEmpty(name)) {
                return defaultLetter;
            }
            char char0 = name.toLowerCase().charAt(0);
            if(Character.isDigit(char0)) {
                return defaultLetter;
            }
            String pinyin = HanziToPinyin.getPinyin(name);
            if(!TextUtils.isEmpty(pinyin)) {
                String letter = pinyin.substring(0, 1).toUpperCase();
                char c = letter.charAt(0);
                if(c < 'A' || c > 'Z') {
                    return defaultLetter;
                }
                return letter;
            }
            return defaultLetter;
        }
    }

    /**
     * Get the boolean settings from resource
     * @param context
     * @param resourceId
     * @return
     */
    public static boolean getBooleanResource(Context context, @BoolRes int resourceId) {
        boolean enable = false;
        try {
            enable = context.getResources().getBoolean(resourceId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return enable;
    }

    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;

    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Used to handle message unread
     * @param count
     * @return
     */
    public static String handleBigNum(int count) {
        if(count <= 99) {
            return String.valueOf(count);
        }else {
            return "99+";
        }
    }
}