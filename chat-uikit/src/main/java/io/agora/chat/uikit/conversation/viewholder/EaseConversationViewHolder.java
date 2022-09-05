package io.agora.chat.uikit.conversation.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.Date;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatRoom;
import io.agora.chat.Conversation;
import io.agora.chat.Group;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.manager.EaseAtMessageHelper;
import io.agora.chat.uikit.manager.EasePreferenceManager;
import io.agora.chat.uikit.models.EaseGroupInfo;
import io.agora.chat.uikit.provider.EaseGroupInfoProvider;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.util.EMLog;

public class EaseConversationViewHolder extends EaseBaseConversationViewHolder{
    public EaseConversationViewHolder(@NonNull View itemView, EaseConversationSetStyle style) {
        super(itemView, style);
    }

    @Override
    public void setData(EaseConversationInfo bean, int position) {
        super.setData(bean, position);
        Conversation item = (Conversation) bean.getInfo();
        Context context = itemView.getContext();
        String username = item.conversationId();
        mentioned.setVisibility(View.GONE);
        int defaultAvatar = 0;
        String showName = null;
        EMLog.e("holder: ",((Conversation) bean.getInfo()).conversationId()+ "  -  " + bean.isMute());
        if (bean.isMute()){
            msgMute.setVisibility(View.VISIBLE);
        }else {
            msgMute.setVisibility(View.GONE);
        }
        if(item.getType() == Conversation.ConversationType.GroupChat) {
            if(EaseAtMessageHelper.get().hasAtMeMsg(username)) {
                mentioned.setText(R.string.ease_chat_were_mentioned);
                mentioned.setVisibility(View.VISIBLE);
            }
            defaultAvatar = R.drawable.ease_default_group_avatar;
            Group group = ChatClient.getInstance().groupManager().getGroup(username);
            showName = group != null ? group.getGroupName() : username;
        }else if(item.getType() == Conversation.ConversationType.ChatRoom) {
            defaultAvatar = R.drawable.ease_default_chatroom_avatar;
            ChatRoom chatRoom = ChatClient.getInstance().chatroomManager().getChatRoom(username);
            showName = chatRoom != null && !TextUtils.isEmpty(chatRoom.getName()) ? chatRoom.getName() : username;
        }else {
            defaultAvatar = R.drawable.ease_default_avatar;
            showName = username;
        }
        avatar.setImageResource(defaultAvatar);
        this.name.setText(showName);
        if(item.getType() != Conversation.ConversationType.Chat) {
            EaseGroupInfoProvider infoProvider = EaseUIKit.getInstance().getGroupInfoProvider();
            if(infoProvider != null) {
                EaseGroupInfo info = infoProvider.getGroupInfo(username, item.getType().ordinal());
                if(info != null) {
                    String name = info.getName();
                    if(!TextUtils.isEmpty(name)) {
                        this.name.setText(name);
                    }
                    String iconUrl = info.getIconUrl();
                    if(!TextUtils.isEmpty(iconUrl)) {
                        try {
                            int resourceId = Integer.parseInt(iconUrl);
                            Glide.with(mContext).load(resourceId).placeholder(defaultAvatar).error(defaultAvatar).into(avatar);
                        } catch (NumberFormatException e) {
                            Glide.with(mContext).load(iconUrl).placeholder(defaultAvatar).error(defaultAvatar).into(avatar);
                        }
                    }else {
                        Glide.with(mContext).load(info.getIcon()).placeholder(defaultAvatar).error(defaultAvatar).into(avatar);
                    }
                    
                    Drawable background = info.getBackground();
                    if(background != null) {
                        itemView.setBackground(background);
                    }
                    EaseGroupInfo.AvatarSettings settings = info.getAvatarSettings();
                    if(settings != null && avatar != null) {
                        if(settings.getAvatarShapeType() != 0)
                            avatar.setShapeType(settings.getAvatarShapeType());
                        if(settings.getAvatarBorderWidth() != 0)
                            avatar.setBorderWidth(settings.getAvatarBorderWidth());
                        if(settings.getAvatarBorderColor() != 0)
                            avatar.setBorderColor(settings.getAvatarBorderColor());
                        if(settings.getAvatarRadius() != 0)
                            avatar.setRadius(settings.getAvatarRadius());
                    }
                }
            }
        }

        // add judgement for conversation type
        if(item.getType() == Conversation.ConversationType.Chat) {
            EaseUserUtils.setUserAvatar(context, username, ContextCompat.getDrawable(context, defaultAvatar), this.avatar.getDrawable(), this.avatar);
            EaseUserUtils.setUserNick(username, this.name);
        }
        if(!setModel.isHideUnreadDot()) {
            showUnreadNum(item.getUnreadMsgCount());
        }

        if(item.getAllMsgCount() != 0) {
            ChatMessage lastMessage = item.getLastMessage();
            message.setText(EaseSmileUtils.getSmiledText(context, EaseUtils.getMessageDigest(lastMessage, context)));
            time.setText(EaseDateUtils.getTimestampString(context, new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == ChatMessage.Direct.SEND && lastMessage.status() == ChatMessage.Status.FAIL) {
                mMsgState.setVisibility(View.VISIBLE);
            } else {
                mMsgState.setVisibility(View.GONE);
            }
        }

        if(mentioned.getVisibility() != View.VISIBLE) {
            String unSendMsg = EasePreferenceManager.getInstance().getUnSendMsgInfo(username);
            if(!TextUtils.isEmpty(unSendMsg)) {
                mentioned.setText(R.string.ease_chat_were_not_send_msg);
                message.setText(unSendMsg);
                mentioned.setVisibility(View.VISIBLE);
            }
        }
    }
}
