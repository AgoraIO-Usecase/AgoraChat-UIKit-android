package com.hyphenate.easeui.model

data class EaseCustomHeaderItem @JvmOverloads constructor(
    var headerId:Int,
    var order: Int = 0,
    var headerIconRes:Int?= -1,
    var headerTitle:String?= null,
    var headerContent:String?= null,
    var headerEndIconRes:Int? = -1,
    var headerUnReadCount:Int = 0,
    var headerItemDivider:Boolean? = true,
    var headerItemShowArrow:Boolean? = false
)