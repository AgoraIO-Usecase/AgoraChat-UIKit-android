package com.hyphenate.easeui.viewmodel.search

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.common.ChatSearchDirection
import com.hyphenate.easeui.common.ChatSearchScope
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.feature.search.interfaces.IEaseSearchResultView
import com.hyphenate.easeui.repository.EaseSearchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class EaseSearchViewModel(
    private val stopTimeoutMillis: Long = 5000
): EaseBaseViewModel<IEaseSearchResultView>(),IEaseSearchRequest{

    private val repository: EaseSearchRepository = EaseSearchRepository()

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