package io.agora.uikit.widget

import android.content.Context
import android.util.AttributeSet
import io.agora.uikit.common.IRefresh
import io.agora.uikit.common.OnLoadMoreListener
import io.agora.uikit.common.OnRefreshListener
import io.agora.uikit.common.SwipeRefreshLayout

class RefreshLayout @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null
): SwipeRefreshLayout(context, attrs) {

    override fun finishLoadMore(): IRefresh {
        return super.finishLoadMore()
    }

    override fun finishLoadMoreWithNoMoreData(): IRefresh {
        return super.finishLoadMoreWithNoMoreData()
    }

    override fun finishRefresh(): IRefresh {
        return super.finishRefresh()
    }

    override fun setEnableLoadMore(enable: Boolean): IRefresh {
        return super.setEnableLoadMore(enable)
    }

    override fun setEnableRefresh(enable: Boolean): IRefresh {
        return super.setEnableRefresh(enable)
    }

    override fun setOnRefreshListener(listener: OnRefreshListener?): IRefresh {
        return super.setOnRefreshListener(listener)
    }

    override fun setOnLoadMoreListener(listener: OnLoadMoreListener?): IRefresh {
        return super.setOnLoadMoreListener(listener)
    }

}