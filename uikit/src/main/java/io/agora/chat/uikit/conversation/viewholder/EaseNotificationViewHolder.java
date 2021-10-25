package io.agora.chat.uikit.conversation.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.Date;

import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.models.EaseConvSet;
import io.agora.chat.uikit.provider.EaseConversationInfoProvider;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUtils;

public class EaseNotificationViewHolder extends EaseBaseConversationViewHolder{
    public EaseNotificationViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setData(EaseConversationInfo bean, int position) {
        super.setData(bean, position);
        Conversation item = (Conversation) bean.getInfo();
        Context context = itemView.getContext();
        String username = item.conversationId();
        mentioned.setVisibility(View.GONE);
        EaseConversationInfoProvider infoProvider = EaseUIKit.getInstance().getConversationInfoProvider();
        //avatar.setImageResource(R.drawable.em_system_nofinication);
        //name.setText(mContext.getString(R.string.ease_conversation_system_message));
        if(infoProvider != null) {
            EaseConvSet convSet = infoProvider.getConversationInfo(username);
            if(convSet != null) {
                String name = convSet.getName();
                if(!TextUtils.isEmpty(name)) {
                    this.name.setText(name);
                }
                Drawable icon = convSet.getIcon();
                if(icon != null) {
                    Glide.with(mContext).load(icon).error(R.drawable.ease_system_nofinication).into(avatar);
                }
                Drawable background = convSet.getBackground();
                if(background != null) {
                    itemView.setBackground(background);
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
            time.setText(EaseDateUtils.getTimestampString(itemView.getContext(), new Date(lastMessage.getMsgTime())));
        }
    }
}
