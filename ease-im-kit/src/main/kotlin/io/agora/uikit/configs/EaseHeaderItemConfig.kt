package io.agora.uikit.configs

import android.content.Context
import io.agora.uikit.R
import io.agora.uikit.model.EaseCustomHeaderItem
import java.util.Collections

class EaseHeaderItemConfig(
    val context: Context,
    private var headerItemModels: MutableList<EaseCustomHeaderItem>? = null,
) {
    init {
        if (headerItemModels == null){
            headerItemModels = mutableListOf()
            for (i in defaultHeaderItemString.indices) {
                registerHeaderItemModels(
                    itemId = defaultHeaderItemIds[i],
                    nameRes = defaultHeaderItemString[i],
                    arrowVisible = defaultHeaderShowArrow[i]
                )
            }
        }
    }

    fun getDefaultHeaderItemModels(): MutableList<EaseCustomHeaderItem>?{
        return headerItemModels
    }

    private fun registerHeaderItemModels(
        itemId: Int,startDrawableRes: Int? = null, nameRes: Int, contentRes: Int? = null,endDrawableRes: Int? = null,
        dividerVisible: Boolean? = true, arrowVisible: Boolean? = false, order: Int = 0
    ) {
        registerHeaderItem(
            itemId,startDrawableRes,context.getString(nameRes),
            contentRes?.let { context.getString(it) },
            endDrawableRes,dividerVisible,arrowVisible,order
        )
    }

    private fun registerHeaderItem(itemId: Int, startDrawableRes: Int? ,name: String?,content:String?,
        endDrawableRes: Int?, dividerVisible: Boolean?, arrowVisible: Boolean?, order: Int
    ) {
        val item = EaseCustomHeaderItem(
            headerId = itemId,
            headerIconRes = startDrawableRes,
            headerTitle = name ?: "",
            headerContent = content ?: "",
            headerEndIconRes = endDrawableRes,
            headerItemDivider = dividerVisible,
            headerItemShowArrow = arrowVisible,
            order = order
        )
        headerItemModels?.let {
            it.add(item)
            sortByOrder(it)
        }
    }


    private fun sortByOrder(itemModels: List<EaseCustomHeaderItem>) {
        Collections.sort(itemModels) { o1, o2 ->
            val `val` = o1.order - o2.order
            if (`val` > 0) {
                1
            } else if (`val` == 0) {
                0
            } else {
                -1
            }
        }
    }

}

val defaultHeaderItemString = intArrayOf(
    R.string.ease_header_request, R.string.ease_header_group
)

val defaultHeaderItemIds = intArrayOf(
    R.id.ease_contact_header_new_request,
    R.id.ease_contact_header_group,
)

val defaultHeaderShowArrow = booleanArrayOf(true,true)