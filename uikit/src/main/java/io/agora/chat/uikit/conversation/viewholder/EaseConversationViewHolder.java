package io.agora.chat.uikit.conversation.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

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
import io.agora.chat.uikit.manager.EaseAtMessageHelper;
import io.agora.chat.uikit.manager.EasePreferenceManager;
import io.agora.chat.uikit.models.EaseConvSet;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.provider.EaseConversationInfoProvider;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUtils;

public class EaseConversationViewHolder extends EaseBaseConversationViewHolder{
    public EaseConversationViewHolder(@NonNull View itemView) {
        super(itemView);
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
        if(item.getType() == Conversation.ConversationType.GroupChat) {
            if(EaseAtMessageHelper.get().hasAtMeMsg(username)) {
                mentioned.setText(R.string.ease_chat_were_mentioned);
                mentioned.setVisibility(View.VISIBLE);
            }
            defaultAvatar = R.drawable.ease_default_group_avatar;
            Group group = ChatClient.getInstance().groupManager().getGroup(username);
            showName = group != null ? group.getGroupName() : username;
        }else if(item.getType() == Conversation.ConversationType.ChatRoom) {
            defaultAvatar = R.drawable.ease_default_group_avatar;
            ChatRoom chatRoom = ChatClient.getInstance().chatroomManager().getChatRoom(username);
            showName = chatRoom != null && !TextUtils.isEmpty(chatRoom.getName()) ? chatRoom.getName() : username;
        }else {
            defaultAvatar = R.drawable.ease_default_avatar;
            showName = username;
        }
        avatar.setImageResource(defaultAvatar);
        this.name.setText(showName);
        EaseConversationInfoProvider infoProvider = EaseUIKit.getInstance().getConversationInfoProvider();
        if(infoProvider != null) {
            EaseConvSet convSet = infoProvider.getConversationInfo(username);
            if(convSet != null) {
                String name = convSet.getName();
                if(!TextUtils.isEmpty(name)) {
                    this.name.setText(name);
                }
                Drawable icon = convSet.getIcon();
                if(icon != null) {
                    Glide.with(mContext).load(icon).error(defaultAvatar).into(avatar);
                }
                Drawable background = convSet.getBackground();
                if(background != null) {
                    itemView.setBackground(background);
                }
            }
        }
        // add judgement for conversation type
        if(item.getType() == Conversation.ConversationType.Chat) {
            EaseUserProfileProvider userProvider = EaseUIKit.getInstance().getUserProvider();
            if(userProvider != null) {
                EaseUser user = userProvider.getUser(username);
                if(user != null) {
                    if(!TextUtils.isEmpty(user.getNickname())) {
                        this.name.setText(user.getNickname());
                    }
                    if(!TextUtils.isEmpty(user.getAvatar())) {
                        Drawable drawable = avatar.getDrawable();
                        Glide.with(mContext)
                                .load(user.getAvatar())
                                .error(drawable)
                                .into(avatar);
                    }
                }
            }
        }
        // TODO: 2021/10/25 add configures
        if(true) {
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
