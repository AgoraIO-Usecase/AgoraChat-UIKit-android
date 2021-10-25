package io.agora.chat.uikit.manager;

import android.text.TextUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseUserUtils;

public class EaseAtMessageHelper {
    private List<String> toAtUserList = new ArrayList<String>();
    private Set<String> atMeGroupList = null;
    private static EaseAtMessageHelper instance = null;
    public synchronized static EaseAtMessageHelper get(){
        if(instance == null){
            instance = new EaseAtMessageHelper();
        }
        return instance;
    }
    
    
    private EaseAtMessageHelper(){
        atMeGroupList = EasePreferenceManager.getInstance().getAtMeGroups();
        if(atMeGroupList == null)
            atMeGroupList = new HashSet<String>();
        
    }
    
    /**
     * add user you want to @
     * @param username
     */
    public void addAtUser(String username){
        synchronized (toAtUserList) {
            if(!toAtUserList.contains(username)){
                toAtUserList.add(username);
            }
        }
        
    }
    
    /**
     * check if be mentioned(@) in the content
     * @param content
     * @return
     */
    public boolean containsAtUsername(String content){
        if(TextUtils.isEmpty(content)){
            return false;
        }
        synchronized (toAtUserList) {
            for(String username : toAtUserList){
                String nick = username;
                if(EaseUserUtils.getUserInfo(username) != null){
                    EaseUser user = EaseUserUtils.getUserInfo(username);
                    if (user != null) {
                        nick = user.getNickname();
                    }
                }
                if(content.contains(nick)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAtAll(String content){
        String atAll = "@" + EaseUIKit.getInstance().getContext().getString(R.string.ease_all_members);
        if(content.contains(atAll)){
            return true;
        }
        return false;
    }
    
    /**
     * get the users be mentioned(@) 
     * @param content
     * @return
     */
    public List<String> getAtMessageUsernames(String content){
        if(TextUtils.isEmpty(content)){
            return null;
        }
        synchronized (toAtUserList) {
            List<String> list = null;
            for(String username : toAtUserList){
                String nick = username;
                if(EaseUserUtils.getUserInfo(username) != null){
                    EaseUser user = EaseUserUtils.getUserInfo(username);
                    if (user != null) {
                        nick = user.getNickname();
                    }
                }
                if(content.contains(nick)){
                    if(list == null){
                        list = new ArrayList<String>();
                    }
                    list.add(username);
                }
            }
            return list;
        }
    }
    
    /**
     * parse the message, get and save group id if I was mentioned(@)
     * @param messages
     */
    public void parseMessages(List<ChatMessage> messages) {
        int size = atMeGroupList.size();
        ChatMessage[] msgs = messages.toArray(new ChatMessage[messages.size()]);
        for(ChatMessage msg : msgs){
            if(msg.getChatType() == ChatMessage.ChatType.GroupChat){
                String groupId = msg.getTo();
                try {
                    JSONArray jsonArray = msg.getJSONArrayAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG);
                    for(int i = 0; i < jsonArray.length(); i++){
                        String username = jsonArray.getString(i);
                        if(ChatClient.getInstance().getCurrentUser().equals(username)){
                            if(!atMeGroupList.contains(groupId)){
                                atMeGroupList.add(groupId);
                                break;
                            }
                        }
                    }
                } catch (Exception e1) {
                    //Determine whether is @ all message
                    String usernameStr = msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, null);
                    if(usernameStr != null){
                        if(usernameStr.toUpperCase().equals(EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL)){
                            if(!atMeGroupList.contains(groupId)){
                                atMeGroupList.add(groupId);
                            }
                        }
                    }
                }
                
                if(atMeGroupList.size() != size){
                    EasePreferenceManager.getInstance().setAtMeGroups(atMeGroupList);
                }
            }
        }
    }
    
    /**
     * get groups which I was mentioned
     * @return
     */
    public Set<String> getAtMeGroups(){
        return atMeGroupList;
    }
    
    /**
     * remove group from the list
     * @param groupId
     */
    public void removeAtMeGroup(String groupId){
        if(atMeGroupList.contains(groupId)){
            atMeGroupList.remove(groupId);
            EasePreferenceManager.getInstance().setAtMeGroups(atMeGroupList);
        }
    }
    
    /**
     * check if the input groupId in atMeGroupList
     * @param groupId
     * @return
     */
    public boolean hasAtMeMsg(String groupId){
        return atMeGroupList.contains(groupId);
    }
    
    public boolean isAtMeMsg(ChatMessage message){
        EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
        if(user != null){
            try {
                JSONArray jsonArray = message.getJSONArrayAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG);
                
                for(int i = 0; i < jsonArray.length(); i++){
                    String username = jsonArray.getString(i);
                    if(username.equals(ChatClient.getInstance().getCurrentUser())){
                        return true;
                    }
                }
            } catch (Exception e) {
                //perhaps is a @ all message
                String atUsername = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, null);
                if(atUsername != null){
                    if(atUsername.toUpperCase().equals(EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL)){
                        return true;
                    }
                }
                return  false;
            }
            
        }
        return false;
    }
    
    public JSONArray atListToJsonArray(List<String> atList){
        JSONArray jArray = new JSONArray();
        int size = atList.size();
        for(int i = 0; i < size; i++){
            String username = atList.get(i);
            jArray.put(username);
        }
        return jArray;
    }

    public void cleanToAtUserList(){
        synchronized (toAtUserList){
            toAtUserList.clear();
        }
    }
}
