package io.agora.chat.uikit.widget.video;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.agora.chat.uikit.models.EaseVideoEntity;
import io.agora.chat.uikit.utils.EaseCompat;
import io.agora.util.PathUtil;
import io.agora.util.VersionUtils;

public class EaseMediaManagerRepository {
    /**
     *Get video files from multimedia library and video storage folder in private directory
     * @param context
     * @return
     */
    public LiveData<List<EaseVideoEntity>> getVideoListFromMediaAndSelfFolder(Context context) {
        List<EaseVideoEntity> mList = new ArrayList<>();
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                , null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // ID:MediaStore.Audio.Media._ID
                int id = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID));

                // title：MediaStore.Audio.Media.TITLE
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                // path：MediaStore.Audio.Media.DATA
                String url = null;
                if (!VersionUtils.isTargetQ(context)) {
                    url = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                }

                // duration：MediaStore.Audio.Media.DURATION
                int duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                // SIZE：MediaStore.Audio.Media.SIZE
                int size = (int) cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                // Last modified time：MediaStore.Audio.DATE_MODIFIED
                long lastModified = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));

                if (size <= 0) {
                    continue;
                }
                Uri uri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);

                EaseVideoEntity entty = new EaseVideoEntity();
                entty.ID = id;
                entty.title = title;
                entty.filePath = url;
                entty.duration = duration;
                entty.size = size;
                entty.uri = uri;
                entty.lastModified = lastModified;
                mList.add(entty);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        getSelfVideoFiles(context, mList);

        if (!mList.isEmpty()) {
            sortVideoEntities(mList);
        }
        return new MutableLiveData<>(mList);
    }

    private void getSelfVideoFiles(Context context, List<EaseVideoEntity> mList) {
        File videoFolder = PathUtil.getInstance().getVideoPath();
        if (videoFolder.exists() && videoFolder.isDirectory()) {
            File[] files = videoFolder.listFiles();
            if (files != null && files.length > 0) {
                EaseVideoEntity entty;
                for (int i = 0; i < files.length; i++) {
                    entty = new EaseVideoEntity();
                    File file = files[i];
                    if (!EaseCompat.isVideoFile(context, file.getName()) || file.length() <= 0) {
                        continue;
                    }
                    entty.filePath = file.getAbsolutePath();
                    entty.size = (int) file.length();
                    entty.title = file.getName();
                    entty.lastModified = file.lastModified();
                    MediaPlayer player = new MediaPlayer();
                    try {
                        player.setDataSource(file.getPath());
                        player.prepare();
                        entty.duration = player.getDuration();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (entty.size <= 0 || entty.duration <= 0) {
                        continue;
                    }
                    mList.add(entty);
                }
            }
        }

    }

    private void sortVideoEntities(List<EaseVideoEntity> mList) {
        Collections.sort(mList, new Comparator<EaseVideoEntity>() {
            @Override
            public int compare(EaseVideoEntity o1, EaseVideoEntity o2) {
                return (int) (o2.lastModified - o1.lastModified);
            }
        });
    }
}

