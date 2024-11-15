package com.hyphenate.easeui.feature.search.interfaces

import com.hyphenate.easeui.common.interfaces.IControlDataView

interface IUIKitSearchResultView: IControlDataView {
    fun searchSuccess(result: Any)
    fun searchBlockUserSuccess(result: Any){}
}