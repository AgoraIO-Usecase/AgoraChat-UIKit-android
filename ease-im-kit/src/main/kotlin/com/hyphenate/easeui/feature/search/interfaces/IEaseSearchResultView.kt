package com.hyphenate.easeui.feature.search.interfaces

import com.hyphenate.easeui.common.interfaces.IControlDataView

interface IEaseSearchResultView: IControlDataView {
    fun searchSuccess(result: Any)
}