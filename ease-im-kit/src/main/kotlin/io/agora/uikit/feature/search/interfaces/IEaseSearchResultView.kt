package io.agora.uikit.feature.search.interfaces

import io.agora.uikit.common.interfaces.IControlDataView

interface IEaseSearchResultView: IControlDataView {
    fun searchSuccess(result: Any)
    fun searchBlockUserSuccess(result: Any){}
}