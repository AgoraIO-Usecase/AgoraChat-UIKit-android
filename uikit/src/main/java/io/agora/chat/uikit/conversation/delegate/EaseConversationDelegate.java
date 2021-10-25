package io.agora.chat.uikit.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

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
import io.agora.chat.uikit.models.EaseConvSet;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.provider.EaseConversationInfoProvider;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUtils;

public class EaseConversationDelegate extends EaseDefaultConversationDelegate {

    public EaseConversationDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public boolean isForViewType(EaseConversationInfo item, int position) {
        return item != null && item.getInfo() instanceof Conversation;
    }

    @Override
    protected void onBindConViewHolder(ViewHolder holder, int position, EaseConversationInfo bean) {
        Conversation item = (Conversation) bean.getInfo();
        Context context = holder.itemView.getContext();
        String username = item.conversationId();
        holder.listIteaseLayout.setBackground(!TextUtils.isEmpty(item.getExtField())
                ? ContextCompat.getDrawable(context, R.drawable.ease_conversation_top_bg)
                : null);
        holder.mentioned.setVisibility(View.GONE);
        int defaultAvatar = 0;
        String showName = null;
        if(item.getType() == Conversation.ConversationType.GroupChat) {
            if(EaseAtMessageHelper.get().hasAtMeMsg(username)) {
                holder.mentioned.setText(R.string.ease_chat_were_mentioned);
                holder.mentioned.setVisibility(View.VISIBLE);
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
        holder.avatar.setImageResource(defaultAvatar);
        holder.name.setText(showName);
        EaseConversationInfoProvider infoProvider = EaseUIKit.getInstance().getConversationInfoProvider();
        if(infoProvider != null) {
            EaseConvSet convSet = infoProvider.getConversationInfo(username);
            if(convSet != null) {
                String name = convSet.getName();
                if(!TextUtils.isEmpty(name)) {
                    holder.name.setText(name);
                }
                Drawable icon = convSet.getIcon();
                if(icon != null) {
                    Glide.with(holder.mContext).load(icon).error(defaultAvatar).into(holder.avatar);
                }
                Drawable background = convSet.getBackground();
                if(background != null) {
                    holder.itemView.setBackground(background);
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
                        holder.name.setText(user.getNickname());
                    }
                    if(!TextUtils.isEmpty(user.getAvatar())) {
                        Drawable drawable = holder.avatar.getDrawable();
                        Glide.with(holder.mContext)
                                .load(user.getAvatar())
                                .error(drawable)
                                .into(holder.avatar);
                    }
                }
            }
        }

        if(!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }

        if(item.getAllMsgCount() != 0) {
            ChatMessage lastMessage = item.getLastMessage();
            holder.message.setText(EaseSmileUtils.getSmiledText(context, EaseUtils.getMessageDigest(lastMessage, context)));
            holder.time.setText(EaseDateUtils.getTimestampString(context, new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == ChatMessage.Direct.SEND && lastMessage.status() == ChatMessage.Status.FAIL) {
                holder.mMsgState.setVisibility(View.VISIBLE);
            } else {
                holder.mMsgState.setVisibility(View.GONE);
            }
        }

        if(holder.mentioned.getVisibility() != View.VISIBLE) {
            String unSendMsg = EasePreferenceManager.getInstance().getUnSendMsgInfo(username);
            if(!TextUtils.isEmpty(unSendMsg)) {
                holder.mentioned.setText(R.string.ease_chat_were_not_send_msg);
                holder.message.setText(unSendMsg);
                holder.mentioned.setVisibility(View.VISIBLE);
            }
        }
    }
}

