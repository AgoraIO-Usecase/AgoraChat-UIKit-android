package io.agora.uikit.common.enums

enum class EaseListViewType(val code: Int) {
    LIST_CONTACT(1001),
    LIST_SELECT_CONTACT(1002),
    LIST_GROUP_MEMBER(1003);

    companion object {
        fun fromCode(code: Int): EaseListViewType? {
            return EaseListViewType.values().find { it.code == code }
        }
    }
}