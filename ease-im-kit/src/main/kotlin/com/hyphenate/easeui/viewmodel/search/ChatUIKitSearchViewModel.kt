package com.hyphenate.easeui.viewmodel.search

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.common.ChatSearchDirection
import com.hyphenate.easeui.common.ChatSearchScope
import com.hyphenate.easeui.viewmodel.ChatUIKitBaseViewModel
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.feature.search.interfaces.IUIKitSearchResultView
import com.hyphenate.easeui.repository.ChatUIKitSearchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class ChatUIKitSearchViewModel(
    private val stopTimeoutMillis: Long = 5000
): ChatUIKitBaseViewModel<IUIKitSearchResultView>(),IUIKitSearchRequest{

    private val repository: ChatUIKitSearchRepository = ChatUIKitSearchRepository()

    companion object{
        const val maxSearchCount:Int = 200
    }

    override fun searchUser(query: String) {
        viewModelScope.launch {
            flow {
                emit(repository.searchUser(query))
            }
            .catchChatException { }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    view?.searchSuccess(it)
                }
            }
        }
    }

    override fun searchBlockUser(query: String) {
        viewModelScope.launch {
            flow {
                emit(repository.searchBlockUser(query))
            }
                .catchChatException { }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        view?.searchBlockUserSuccess(it)
                    }
                }
        }
    }

    override fun searchConversation(query: String) {
        viewModelScope.launch {
            flow {
                emit(repository.searchConversation(query))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    view?.searchSuccess(it)
                }
            }
        }
    }

    override fun searchMessage(
        keywords:String,
        conversationId:String,
        timeStamp:Long,
        from:String?,
        direction:ChatSearchDirection,
        chatScope:ChatSearchScope
    ) {
        viewModelScope.launch {
            flow {
                if (conversationId.isEmpty()){
                    emit(repository.searchMessage(keywords,timeStamp,maxSearchCount,from,direction,chatScope))
                }else{
                    emit(repository.searchMessageByConversation(conversationId,keywords,timeStamp,maxSearchCount,from,direction,ChatSearchScope.CONTENT))
                }
            }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        view?.searchSuccess(it)
                    }
                }
        }
    }


}