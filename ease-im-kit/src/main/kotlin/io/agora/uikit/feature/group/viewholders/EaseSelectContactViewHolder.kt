package io.agora.uikit.feature.group.viewholders

import android.text.TextUtils
import android.view.View
import androidx.viewbinding.ViewBinding
import io.agora.uikit.EaseIM
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.extensions.toProfile
import io.agora.uikit.feature.contact.adapter.EaseContactListAdapter
import io.agora.uikit.databinding.EaseLayoutGroupSelectContactBinding
import io.agora.uikit.feature.search.interfaces.OnContactSelectListener
import io.agora.uikit.model.EaseUser
import io.agora.uikit.provider.getSyncUser

open class EaseSelectContactViewHolder(
    val viewBinding:EaseLayoutGroupSelectContactBinding,
) : EaseBaseRecyclerViewAdapter.ViewHolder<EaseUser>(binding = viewBinding) {
    protected var selectedListener: OnContactSelectListener?=null
    private var user:EaseUser?=null
    private var position:Int = 0
    protected var checkedList:MutableList<String> = mutableListOf()

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        (viewBinding as EaseLayoutGroupSelectContactBinding).let {

            it.itemLayout.setOnClickListener{ view->
                val isChecked = it.cbSelect.isChecked
                it.cbSelect.isChecked = !isChecked
            }

            it.cbSelect.setOnCheckedChangeListener{ view,isChecked->
                user?.let { u->
                    selectedListener?.onContactSelectedChanged(view,u.userId,isChecked)
                }
            }
        }
    }

    fun setSelectedMembers(selectedMember:MutableList<String>){
        this.checkedList = selectedMember
    }

    override fun setData(item: EaseUser?, position: Int) {
        this.user = item
        this.position = position

        item?.run {
            with(viewBinding) {

                cbSelect.isChecked = checkedList.isNotEmpty() && isContains(checkedList,item.userId)

                emPresence.setUserAvatarData(item.toProfile())
                tvName.text = item.nickname ?: item.userId

                letterHeader.visibility = View.GONE
                if (position == 0 || initialLetter != null && adapter is EaseContactListAdapter
                    && initialLetter != (adapter as EaseContactListAdapter).getItem(position - 1)?.initialLetter) {
                    if (!TextUtils.isEmpty(initialLetter)) {
                        letterHeader.visibility = View.VISIBLE
                        letterHeader.text = initialLetter
                    }
                }

                // Set custom data provided by user
                EaseIM.getUserProvider()?.getSyncUser(userId)?.let { profile ->
                    tvName.text = profile.getRemarkOrName()
                }

            }
        }
    }

    protected fun isContains(data: MutableList<String>?, username: String): Boolean {
        return data != null && data.contains(username)
    }

    fun setCheckBoxSelectListener(listener: OnContactSelectListener?){
        this.selectedListener = listener
    }

}