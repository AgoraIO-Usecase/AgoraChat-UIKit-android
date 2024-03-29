package io.agora.chat.uikit.chat.viewholder;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.NormalFileMessageBody;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;
import io.agora.chat.uikit.utils.EaseCompat;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.exceptions.ChatException;


public class EaseFileViewHolder extends EaseChatRowViewHolder {

    public EaseFileViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
        Uri filePath = fileMessageBody.getLocalUri();
        EaseFileUtils.takePersistableUriPermission(getContext(), filePath);
        if(EaseFileUtils.isFileExistByUri(getContext(), filePath)){
            EaseCompat.openFile(getContext(), filePath);
        } else {
            // download the file
            EaseActivityProviderHelper.startToDownloadFileActivity(getContext(), message);
        }
        if (message.direct() == ChatMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == ChatMessage.ChatType.Chat) {
            try {
                ChatClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (ChatException e) {
                e.printStackTrace();
            }
        }
    }
}
