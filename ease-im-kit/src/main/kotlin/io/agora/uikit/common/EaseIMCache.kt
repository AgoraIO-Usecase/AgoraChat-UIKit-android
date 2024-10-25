package io.agora.uikit.common

import io.agora.uikit.common.enums.EaseCacheType
import io.agora.uikit.common.helper.EasePreferenceManager
import io.agora.uikit.model.EaseGroupProfile
import io.agora.uikit.model.EasePreview
import io.agora.uikit.model.EaseProfile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class EaseIMCache {
    private val userMap: ConcurrentMap<String, EaseProfile> = ConcurrentHashMap()
    // Cache the group info. The key is the groupId, the value is the group info.
    private val groupMap: ConcurrentMap<String, EaseGroupProfile> = ConcurrentHashMap()
    // Cache the userinfo parsed by message ext. The key is the userId, the value is the userinfo.
    private val messageUserMap: ConcurrentMap<String, EaseProfile> = ConcurrentHashMap()
    private val mutedConvMap: MutableMap<String, Long> = HashMap()
    private val previewMap:ConcurrentMap<String, EasePreview> = ConcurrentHashMap()
    private val checkPreviewMap:MutableMap<String,Boolean> = mutableMapOf()

    companion object {
        private const val TAG = "EaseIMCache"
    }

    fun init() {
        clear(EaseCacheType.ALL)
        // Load the muted conversation list from the local storage.
        val muteMap = EasePreferenceManager.getInstance().getMuteMap(ChatClient.getInstance().currentUser)
        if (muteMap.isNotEmpty()) {
            mutedConvMap.putAll(muteMap)
        }
    }

    fun insertUser(user: EaseProfile) {
        userMap[user.id] = user
    }

    /**
     * Insert or update the group info to the cache.
     * @param groupId The group id.
     * @param profile The group info.
     */
    fun insertGroup(groupId: String?, profile: EaseGroupProfile?) {
        if (groupId.isNullOrEmpty()) {
            ChatLog.e(TAG, "insertGroup: groupId is null or empty")
            return
        }
        groupMap[groupId] = profile
    }

    fun getUser(userId: String?): EaseProfile? {
        if (userId.isNullOrEmpty()) {
            return null
        }
        return userMap[userId]
    }

    /**
     * Get the group info by groupId.
     * @param groupId The group id.
     * @return The group info.
     */
    fun getGroup(groupId: String?): EaseGroupProfile? {
        if (groupId.isNullOrEmpty()) {
            return null
        }
        return groupMap[groupId]
    }

    /**
     * Insert message userinfo to cache.
     */
    @Synchronized
    fun insertMessageUser(userId: String, profile: EaseProfile) {
        if (messageUserMap.containsKey(userId)) {
            if (messageUserMap[userId]!!.getTimestamp() < profile.getTimestamp()) {
                return
            }
        }
        messageUserMap[userId] = profile
    }

    /**
     * Get userinfo cache by userId.
     */
    fun getMessageUserInfo(userId: String?): EaseProfile? {
        if (userId.isNullOrEmpty() || !messageUserMap.containsKey(userId)) return null
        return messageUserMap[userId]
    }

    /**
     * Get the muted conversation list.
     */
    @Synchronized
    fun getMutedConversationList(): MutableMap<String, Long> {
        return mutedConvMap
    }

    /**
     * Add target conversation to mute map.
     */
    @Synchronized
    fun setMutedConversation(conversationId: String, mutedTime: Long = 0) {
        mutedConvMap[conversationId] = mutedTime
        EasePreferenceManager.getInstance().setMuteMap(ChatClient.getInstance().currentUser, mutedConvMap)
    }

    /**
     * Remove target conversation from mute map.
     */
    @Synchronized
    fun removeMutedConversation(conversationId: String) {
        mutedConvMap.remove(conversationId)
        EasePreferenceManager.getInstance().setMuteMap(ChatClient.getInstance().currentUser, mutedConvMap)
    }

    fun clear(type: EaseCacheType?) {
        if (type == null || type == EaseCacheType.ALL) {
            userMap.clear()
            groupMap.clear()
            messageUserMap.clear()
            mutedConvMap.clear()
            previewMap.clear()
        } else {
            when (type) {
                EaseCacheType.CONTACT -> userMap.clear()
                EaseCacheType.CONVERSATION_INFO -> groupMap.clear()
                else -> {
                }
            }
        }
    }

    fun updateProfiles(profiles: List<EaseGroupProfile>) {
        if (profiles.isNotEmpty()) {
            profiles.forEach {
                groupMap[it.id] = it
            }
        }
    }

    fun updateUsers(users: List<EaseProfile>) {
        if (users.isNotEmpty()) {
            users.forEach {
                userMap[it.id] = it
            }
        }
    }

    fun saveUrlPreviewInfo(msgId:String?,bean:EasePreview){
        msgId?.let {
            if (it.isNotEmpty()){
                previewMap[msgId] = bean
            }
        }
    }

    fun getUrlPreviewInfo(msgId: String?): EasePreview? {
        msgId?.let {
            if (previewMap.size > 0 && it.isNotEmpty()) {
                if (previewMap.containsKey(msgId)) {
                    return previewMap[msgId]
                }
            }
        }
        return null
    }

    fun cleanUrlPreviewInfo(msgId: String?){
        msgId?.let {
            if (previewMap.containsKey(it)) {
                previewMap.remove(it)
            }
            if (checkPreviewMap.containsKey(it)){
                checkPreviewMap.remove(it)
            }
        }
    }

    fun checkUrlPreview(msgId:String?,isFirst:Boolean? = true){
        msgId?.let {
            if (it.isNotEmpty()){
                checkPreviewMap[msgId] = isFirst?:true
            }
        }
    }

    fun isFirstLoadedUrlPreview(msgId:String?):Boolean{
        msgId?.let {
            if (checkPreviewMap.isNotEmpty() && it.isNotEmpty()) {
                if (checkPreviewMap.containsKey(msgId)) {
                    return checkPreviewMap[msgId]?:true
                }
            }
        }
        return true
    }

}