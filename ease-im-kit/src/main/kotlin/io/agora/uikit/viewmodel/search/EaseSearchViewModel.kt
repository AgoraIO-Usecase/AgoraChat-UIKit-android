package io.agora.uikit.viewmodel.search

import androidx.lifecycle.viewModelScope
import io.agora.uikit.common.ChatSearchDirection
import io.agora.uikit.common.ChatSearchScope
import io.agora.uikit.viewmodel.EaseBaseViewModel
import io.agora.uikit.common.extensions.catchChatException
import io.agora.uikit.feature.search.interfaces.IEaseSearchResultView
import io.agora.uikit.repository.EaseSearchRepository
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