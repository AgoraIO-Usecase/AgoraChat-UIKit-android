/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agora.chat.uikit.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.MessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.uikit.R;
import io.agora.util.EMLog;
import io.agora.util.ImageUtils;
import io.agora.util.PathUtil;

public class EaseImageUtils extends ImageUtils {

    public static String getImagePath(String remoteUrl) {
        String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
        EMLog.d("msg", "image path:" + path);
        return path;

    }

    public static String getImagePathByFileName(String filename) {
        String path = PathUtil.getInstance().getImagePath() + "/" + filename;
        EMLog.d("msg", "image path:" + path);
        return path;

    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

    public static String getThumbnailImagePathByName(String filename) {
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + filename;
        EMLog.d("msg", "thum image dgdfg path:" + path);
        return path;
    }

    /**
     * Get the maximum length and width of the picture
     * @param context
     */
    public static int[] getImageMaxSize(Context context) {
        float[] screenInfo = EaseUtils.getScreenInfo(context);
        int[] maxSize = new int[2];
        if (screenInfo != null) {
            maxSize[0] = (int) (screenInfo[0] / 3);
            maxSize[1] = (int) (screenInfo[0] / 2);
        }
        return maxSize;
    }

    /**
     * Show video cover
     * @param context
     * @param imageView
     * @param message
     * @return
     */
    public static ViewGroup.LayoutParams showVideoThumb(Context context, ImageView imageView, ChatMessage message) {
        MessageBody body = message.getBody();
        if (!(body instanceof VideoMessageBody)) {
            return imageView.getLayoutParams();
        }
        int width = ((VideoMessageBody) body).getThumbnailWidth();
        int height = ((VideoMessageBody) body).getThumbnailHeight();
        Uri localThumbUri = ((VideoMessageBody) body).getLocalThumbUri();
        EaseFileUtils.takePersistableUriPermission(context, localThumbUri);
        String thumbnailUrl = ((VideoMessageBody) body).getThumbnailUrl();
        if (!EaseFileUtils.isFileExistByUri(context, localThumbUri)) {
            localThumbUri = null;
        }
        return showImage(context, imageView, localThumbUri, thumbnailUrl, width, height);
    }

    public static ViewGroup.LayoutParams getImageShowSize(Context context, ChatMessage message) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        MessageBody body = message.getBody();
        if (!(body instanceof ImageMessageBody)) {
            return params;
        }
        int width = ((ImageMessageBody) body).getWidth();
        int height = ((ImageMessageBody) body).getHeight();
        Uri imageUri = ((ImageMessageBody) body).getLocalUri();
        if (!EaseFileUtils.isFileExistByUri(context, imageUri)) {
            imageUri = ((ImageMessageBody) body).thumbnailLocalUri();
            if (!EaseFileUtils.isFileExistByUri(context, imageUri)) {
                imageUri = null;
            }
        }
        if (width == 0 || height == 0) {
            BitmapFactory.Options options = null;
            try {
                options = ImageUtils.getBitmapOptions(context, imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (options != null) {
                width = options.outWidth;
                height = options.outHeight;
            }
        }
        int[] maxSize = getImageMaxSize(context);
        int maxWidth = maxSize[0];
        int maxHeight = maxSize[1];

        float mRadio = maxWidth * 1.0f / maxHeight;
        float radio = width * 1.0f / (height == 0 ? 1 : height);
        if (radio == 0) {
            radio = 1;
        }
        if ((maxHeight == 0 && maxWidth == 0) /*|| (width <= maxWidth && height <= maxHeight)*/) {
            return params;
        }
        if (mRadio / radio < 0.1f) {
            params.width = maxWidth;
            params.height = maxWidth / 2;
        } else if (mRadio / radio > 4) {
            params.width = maxHeight / 2;
            params.height = maxHeight;
        } else {
            if (radio < mRadio) {
                params.height = maxHeight;
                params.width = (int) (maxHeight * radio);
            } else {
                params.width = maxWidth;
                params.height = (int) (maxWidth / radio);
            }
        }
        return params;
    }

    /**
     * Show picture
     * @param context
     * @param imageView
     * @param message
     * @return
     */
    public static ViewGroup.LayoutParams showImage(Context context, ImageView imageView, ChatMessage message) {
        MessageBody body = message.getBody();
        if (!(body instanceof ImageMessageBody)) {
            return imageView.getLayoutParams();
        }
        int width = ((ImageMessageBody) body).getWidth();
        int height = ((ImageMessageBody) body).getHeight();
        Uri imageUri = ((ImageMessageBody) body).getLocalUri();
        EaseFileUtils.takePersistableUriPermission(context, imageUri);
        EMLog.e("tag", "current show small view big file: uri:" + imageUri + " exist: " + EaseFileUtils.isFileExistByUri(context, imageUri));
        if (!EaseFileUtils.isFileExistByUri(context, imageUri)) {
            imageUri = ((ImageMessageBody) body).thumbnailLocalUri();
            EaseFileUtils.takePersistableUriPermission(context, imageUri);
            EMLog.e("tag", "current show small view thumbnail file: uri:" + imageUri + " exist: " + EaseFileUtils.isFileExistByUri(context, imageUri));
            if (!EaseFileUtils.isFileExistByUri(context, imageUri)) {
                //context.revokeUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUri = null;
            }
        }
        if (width == 0 || height == 0) {
            BitmapFactory.Options options = null;
            try {
                options = ImageUtils.getBitmapOptions(context, imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (options != null) {
                width = options.outWidth;
                height = options.outHeight;
            }
        }
        String thumbnailUrl = null;
        // If not auto download thumbnail, do not set remote url
        if (ChatClient.getInstance().getOptions().getAutodownloadThumbnail()) {
            thumbnailUrl = ((ImageMessageBody) body).getThumbnailUrl();
            if (TextUtils.isEmpty(thumbnailUrl)) {
                thumbnailUrl = ((ImageMessageBody) body).getRemoteUrl();
            }
        }
        return showImage(context, imageView, imageUri, thumbnailUrl, width, height);
    }

    /**
     * The logic of displaying pictures is as follows:
     * 1. The width of the picture does not exceed 1/3 of the screen width,
     * and the height does not exceed 1/2 of the screen width. In this case,
     * the aspect ratio of the picture is 3:2
     * 2. If the aspect ratio of the picture is greater than 3:2,
     * select the height direction to be consistent with the regulations,
     * and the width direction will be scaled proportionally
     * 3. If the aspect ratio of the picture is less than 3:2,
     * select the width direction to be consistent with the regulations,
     * and the height direction is scaled proportionally
     * 4. If the length and width of the picture are small,
     * just display it according to the size of the picture
     * 5. If there is no local resource, show the server address
     * @param context
     * @param imageView
     * @param imageUri Picture local resources
     * @param imageUrl Server picture address
     * @param imgWidth
     * @param imgHeight
     * @return
     */
    public static ViewGroup.LayoutParams showImage(Context context, ImageView imageView, Uri imageUri, String imageUrl, int imgWidth, int imgHeight) {
        int[] maxSize = getImageMaxSize(context);
        int maxWidth = maxSize[0];
        int maxHeight = maxSize[1];

        float mRadio = maxWidth * 1.0f / maxHeight;
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        float radio = imgWidth * 1.0f / (imgHeight == 0 ? 1 : imgHeight);
        if (radio == 0) {
            radio = 1;
        }

        if ((maxHeight == 0 && maxWidth == 0) /*|| (width <= maxWidth && height <= maxHeight)*/) {
            if (context instanceof Activity && (((Activity) context).isFinishing() || ((Activity) context).isDestroyed())) {
                return imageView.getLayoutParams();
            }
            Glide.with(context).load(imageUri == null ? imageUrl : imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            return imageView.getLayoutParams();
        }
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (mRadio / radio < 0.1f) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.width = maxWidth;
            params.height = maxWidth / 2;
        } else if (mRadio / radio > 4) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.width = maxHeight / 2;
            params.height = maxHeight;
        } else {
            if (radio < mRadio) {
                params.height = maxHeight;
                params.width = (int) (maxHeight * radio);
            } else {
                params.width = maxWidth;
                params.height = (int) (maxWidth / radio);
            }
        }
        if (context instanceof Activity && (((Activity) context).isFinishing() || ((Activity) context).isDestroyed())) {
            return params;
        }
        Glide.with(context)
                .load(imageUri == null ? imageUrl : imageUri)
                .apply(new RequestOptions()
                        .error(R.drawable.ease_default_image))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(params.width, params.height)
                .into(imageView);
        return params;
    }

    public static void setDrawableSize(TextView textView, float defaultSize) {
        float mLeftHeight = defaultSize;
        float mLeftWidth = defaultSize;
        float mRightHeight = defaultSize;
        float mRightWidth = defaultSize;
        Drawable[] existingAbs = textView.getCompoundDrawables();
        Drawable left = existingAbs[0];
        Drawable right = existingAbs[2];

        if (left != null && (mLeftWidth != 0 && mLeftHeight != 0)) {
            left.setBounds(0, 0, (int) mLeftWidth, (int) mLeftHeight);
        }
        if (right != null && (mRightWidth != 0 && mRightHeight != 0)) {
            right.setBounds(0, 0, (int) mRightWidth, (int) mRightHeight);
        }
        textView.setCompoundDrawables(
                left != null ? left : existingAbs[0],
                existingAbs[1],
                right != null ? right : existingAbs[2],
                existingAbs[3]
        );

    }

}
