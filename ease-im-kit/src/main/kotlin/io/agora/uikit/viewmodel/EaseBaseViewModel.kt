package io.agora.uikit.viewmodel

import androidx.lifecycle.ViewModel
import io.agora.uikit.common.interfaces.IControlDataView

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