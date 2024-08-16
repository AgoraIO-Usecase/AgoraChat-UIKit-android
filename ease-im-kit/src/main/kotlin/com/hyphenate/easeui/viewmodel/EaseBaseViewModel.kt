package com.hyphenate.easeui.viewmodel

import androidx.lifecycle.ViewModel
import com.hyphenate.easeui.common.interfaces.IControlDataView

abstract class EaseBaseViewModel<V: IControlDataView>: ViewModel(), IAttachView {
    protected var view: V? = null

    override fun attachView(view: IControlDataView) {
        this.view = view as? V
    }
    override fun detachView() {
        this.view = null
    }
    override fun onCleared() {
        detachView()
        super.onCleared()
    }

}