package io.agora.chat.uikit.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.Date;

import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.models.EaseConvSet;
import io.agora.chat.uikit.manager.EaseNotificationMsgManager;
import io.agora.chat.uikit.provider.EaseConversationInfoProvider;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUtils;

public class EaseSystemMsgDelegate extends EaseDefaultConversationDelegate {

    public EaseSystemMsgDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public boolean isForViewType(EaseConversationInfo item, int position) {
        return item != null && item.getInfo() instanceof Conversation
                && EaseNotificationMsgManager.getInstance().isNotificationConversation((Conversation) item.getInfo());
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
        EaseConversationInfoProvider infoProvider = EaseUIKit.getInstance().getConversationInfoProvider();
        //holder.avatar.setImageResource(R.drawable.em_system_nofinication);
        //holder.name.setText(holder.mContext.getString(R.string.ease_conversation_system_message));
        if(infoProvider != null) {
            EaseConvSet convSet = infoProvider.getConversationInfo(username);
            if(convSet != null) {
                String name = convSet.getName();
                if(!TextUtils.isEmpty(name)) {
                    holder.name.setText(name);
                }
                Drawable icon = convSet.getIcon();
                if(icon != null) {
                    //Glide.with(holder.mContext).load(icon).error(R.drawable.em_system_nofinication).into(holder.avatar);
                }
                Drawable background = convSet.getBackground();
                if(background != null) {
                    holder.itemView.setBackground(background);
                }
            }
        }

        if(!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }

        if(item.getAllMsgCount() != 0) {
            ChatMessage lastMessage = item.getLastMessage();
            holder.message.setText(EaseSmileUtils.getSmiledText(context, EaseUtils.getMessageDigest(lastMessage, context)));
            holder.time.setText(EaseDateUtils.getTimestampString(holder.itemView.getContext(), new Date(lastMessage.getMsgTime())));
        }
    }
}

