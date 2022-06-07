package io.agora.chat.uikit.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.options.EaseAvatarOptions;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;
import io.agora.chat.uikit.widget.EaseImageView;


public class EaseUserUtils {

    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        EaseUserProfileProvider provider = EaseUIKit.getInstance().getUserProvider();
        return provider == null ? null : provider.getUser(username);
    }

    /**
     * set user's avatar style
     * @param imageView
     */
    public static void setUserAvatarStyle(EaseImageView imageView) {
        EaseAvatarOptions avatarOptions = EaseUIKit.getInstance().getAvatarOptions();
        if(avatarOptions == null || imageView == null) {
            return;
        }
        if(avatarOptions.getAvatarShape() != 0)
            imageView.setShapeType(avatarOptions.getAvatarShape());
        if(avatarOptions.getAvatarBorderWidth() != 0)
            imageView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
        if(avatarOptions.getAvatarBorderColor() != 0)
            imageView.setBorderColor(avatarOptions.getAvatarBorderColor());
        if(avatarOptions.getAvatarRadius() != 0)
            imageView.setRadius(avatarOptions.getAvatarRadius());
    }
    
    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	setUserAvatar(context, username, null, null, imageView);
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, Drawable placeholder, Drawable errorDrawable, ImageView imageView){
        if(placeholder == null) {
            placeholder = ContextCompat.getDrawable(context, R.drawable.ease_default_avatar);
        }
        if(errorDrawable == null) {
            errorDrawable = ContextCompat.getDrawable(context, R.drawable.ease_default_avatar);
        }
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context)
                        .load(avatarResId)
                        .placeholder(placeholder)
                        .error(errorDrawable)
                        .into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar())
                        .apply(RequestOptions
                                .placeholderOf(placeholder)
                                .error(errorDrawable)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imageView);
            }
        }else{
            Glide.with(context).load(placeholder).into(imageView);
        }
    }

    /**
     * show user avatar
     * @param context
     * @param avatar
     * @param imageView
     */
    public static void showUserAvatar(Context context, String avatar, ImageView imageView) {
        if(TextUtils.isEmpty(avatar)) {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
            return;
        }
        try {
            int avatarResId = Integer.parseInt(avatar);
            Glide.with(context).load(avatarResId).into(imageView);
        } catch (Exception e) {
            //use default avatar
            Glide.with(context).load(avatar)
                    .apply(RequestOptions.placeholderOf(R.drawable.ease_default_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }
    
    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNickname() != null){
        		textView.setText(user.getNickname());
        	}else{
        		textView.setText(username);
        	}
        }
    }
    
}
