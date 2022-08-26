package io.agora.chat.uikit.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.agora.chat.uikit.EaseUIKit;

public class EasePreferenceManager {
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private static final String KEY_AT_GROUPS = "AT_GROUPS";
    private static String SHARED_KEY_SETTING_RECORD_ON_SERVER = "shared_key_setting_record_on_server";
    private static String SHARED_KEY_SETTING_MERGE_STREAM = "shared_key_setting_merge_stream";
    private static String MUTE_DATA_KEY = "mute_data_key";

    @SuppressLint("CommitPrefEdits")
    private EasePreferenceManager(){
        mSharedPreferences = EaseUIKit.getInstance().getContext().getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }
    private static EasePreferenceManager instance;
    
    public synchronized static EasePreferenceManager getInstance(){
        if(instance == null){
            instance = new EasePreferenceManager();
        }
        return instance;
        
    }
    
    
    public void setAtMeGroups(Set<String> groups) {
        editor.remove(KEY_AT_GROUPS);
        editor.putStringSet(KEY_AT_GROUPS, groups);
        editor.apply();
    }
    
    public Set<String> getAtMeGroups(){
        return mSharedPreferences.getStringSet(KEY_AT_GROUPS, null);
    }

    public void setRecordOnServer(boolean value) {
        editor.putBoolean(SHARED_KEY_SETTING_RECORD_ON_SERVER, value);
        editor.apply();
    }

    public boolean isRecordOnServer() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_RECORD_ON_SERVER, false);
    }

    public void setMergeStream(boolean value) {
        editor.putBoolean(SHARED_KEY_SETTING_MERGE_STREAM, value);
        editor.apply();
    }

    public boolean isMergeStream() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_MERGE_STREAM, false);
    }


    /**
     * Save unsent text message content
     * @param toChatUsername
     * @param content
     */
    public void saveUnSendMsgInfo(String toChatUsername, String content) {
        editor.putString(toChatUsername, content);
        editor.apply();
    }

    public String getUnSendMsgInfo(String toChatUsername) {
        return mSharedPreferences.getString(toChatUsername, "");
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }


    public void setMuteMap(Map<String, Long> data){
        Map<String, Long> muteMap = getMuteMap();
        muteMap.putAll(data);
        JSONArray mJsonArray = new JSONArray();
        Iterator<Map.Entry<String, Long>> iterator = muteMap.entrySet().iterator();
        JSONObject object = new JSONObject();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
            }
        }
        mJsonArray.put(object);
        editor.putString(MUTE_DATA_KEY, mJsonArray.toString());
        editor.commit();
    }

    public Map<String,Long> getMuteMap(){
        Map<String, Long> mute = new HashMap<>();
        String result = mSharedPreferences.getString(MUTE_DATA_KEY, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        Long value = itemObject.getLong(name);
                        mute.put(name, value);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mute;
    }

    public void removeMute(String key){
        Map<String,Long> map = getMuteMap();
        map.remove(key);
        JSONArray mJsonArray = new JSONArray();
        Iterator<Map.Entry<String, Long>> iterator = map.entrySet().iterator();
        JSONObject object = new JSONObject();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
            }
        }
        mJsonArray.put(object);
        editor.putString(MUTE_DATA_KEY, mJsonArray.toString());
        editor.commit();
    }
}
