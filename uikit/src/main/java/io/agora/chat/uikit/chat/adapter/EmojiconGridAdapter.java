package io.agora.chat.uikit.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.models.EaseEmojicon.Type;
import io.agora.chat.uikit.utils.EaseSmileUtils;

public class EmojiconGridAdapter extends ArrayAdapter<EaseEmojicon>{

    private Type emojiconType;


    public EmojiconGridAdapter(Context context, int textViewResourceId, List<EaseEmojicon> objects, EaseEmojicon.Type emojiconType) {
        super(context, textViewResourceId, objects);
        this.emojiconType = emojiconType;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            if(emojiconType == Type.BIG_EXPRESSION){
                convertView = View.inflate(getContext(), R.layout.ease_row_big_expression, null);
            }else{
                convertView = View.inflate(getContext(), R.layout.ease_row_expression, null);
            }
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_name);
        EaseEmojicon emojicon = getItem(position);
        convertView.setEnabled(emojicon.isEnableClick());
        if(!emojicon.isEnableClick()) {
            if(textView != null) {
                textView.setText("");
            }
            if (imageView != null) {
                imageView.setImageDrawable(null);
            }
            return convertView;
        }

        if(textView != null && emojicon.getName() != null){
            textView.setText(emojicon.getName());
        }

        if(EaseSmileUtils.DELETE_KEY.equals(emojicon.getEmojiText())){
            imageView.setImageResource(R.drawable.ease_delete_expression);
        }else{
            if(emojicon.getIcon() != 0){
                imageView.setImageResource(emojicon.getIcon());
            }else if(emojicon.getIconPath() != null){
                Glide.with(getContext()).load(emojicon.getIconPath())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_expression))
                        .into(imageView);
            }
        }
        
        
        return convertView;
    }
    
}
