package com.hyphenate.easeui.widget

import android.content.Context
import android.util.AttributeSet
import com.hyphenate.easeui.common.IRefresh
import com.hyphenate.easeui.common.OnLoadMoreListener
import com.hyphenate.easeui.common.OnRefreshListener
import com.hyphenate.easeui.common.SwipeRefreshLayout

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