package io.agora.uikit.viewmodel

import io.agora.uikit.common.interfaces.IControlDataView

interface IAttachView {
    fun attachView(view: IControlDataView)

    fun detachView()
}