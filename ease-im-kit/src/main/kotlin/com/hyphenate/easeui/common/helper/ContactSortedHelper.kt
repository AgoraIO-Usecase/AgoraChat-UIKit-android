package com.hyphenate.easeui.common.helper

import com.hyphenate.easeui.model.EaseUser

object ContactSortedHelper {
    fun sortedList(list:List<EaseUser>):List<EaseUser>{
        val sortedList = list.sortedWith(compareBy(
            { if (it.initialLetter == "#") "ZZZZZ" else it.initialLetter
            }, { it.initialLetter }
        ))
        return sortedList
    }
}