package com.hyphenate.easeui.viewmodel

import com.hyphenate.easeui.common.interfaces.IControlDataView

interface IAttachView {
    fun attachView(view: IControlDataView)

    fun detachView()
}