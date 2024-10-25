package io.agora.uikit.feature.search.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.extensions.toProfile
import io.agora.uikit.databinding.EaseLayoutGroupSelectContactBinding
import io.agora.uikit.interfaces.OnContactSelectedListener
import io.agora.uikit.model.EaseUser
import io.agora.uikit.provider.getSyncUser

class EaseSearchUserAdapter(
    private val isShowCheckBox:Boolean = false
): EaseBaseRecyclerViewAdapter<EaseUser>() {
    private var selectedListener:OnContactSelectedListener? = null
    private var query : String = ""

    companion object{
        private var checkedList:MutableList<String> = mutableListOf()
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseUser> =
        EaseSearchUserHolder(
            isShowCheckBox,
            EaseLayoutGroupSelectContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    override fun onBindViewHolder(holder: ViewHolder<EaseUser>, position: Int) {
        if (holder is EaseSearchUserHolder){
            holder.searchText(query)
            selectedListener?.let {
                holder.setCheckBoxSelectListener(it)
            }
        }
        super.onBindViewHolder(holder, position)
    }

    fun searchText(query: String){
        this.query = query
    }

    fun setCheckBoxSelectListener(listener: OnContactSelectedListener){
        this.selectedListener = listener
    }

    fun resetSelect(){
        checkedList.clear()
        notifyDataSetChanged()
    }

    class EaseSearchUserHolder(
        private val isShowCheckBox:Boolean,
        private val viewBinding: EaseLayoutGroupSelectContactBinding,
    ): ViewHolder<EaseUser>(binding = viewBinding) {
        private var position:Int = 0
        private var user:EaseUser? = null
        private var selectedListener: OnContactSelectedListener?=null
        private var queryContent:String = ""

        override fun setData(item: EaseUser?, position: Int) {
            this.user = item
            this.position = position

            item?.run {
                with(viewBinding) {

                    if (isShowCheckBox){
                        cbSelect.visibility = View.VISIBLE

                        itemLayout.setOnClickListener{ view->
                            val isChecked = cbSelect.isChecked
                            cbSelect.isChecked = !isChecked
                        }

                        cbSelect.isChecked = checkedList.isNotEmpty() && isContains(checkedList,item.userId)

                        cbSelect.setOnCheckedChangeListener{ view,isChecked->
                            item.let { u->
                                if (isChecked) {
                                    checkedList.let { data ->
                                        if (!data.contains(u.userId)){
                                            data.add(u.userId)
                                        } else { }
                                    }
                                }else{
                                    checkedList.remove(u.userId)
                                }
                            }
                            selectedListener?.onContactSelectedChanged(view, checkedList)
                        }
                    }else{
                        cbSelect.visibility = View.GONE
                    }

                    emPresence.setUserAvatarData(item.toProfile())
                    var title  = item.nickname ?: item.userId

                    EaseIM.getUserProvider()?.getSyncUser(item.userId)?.let {
                        emPresence.setUserAvatarData(it)
                        title = it.getRemarkOrName()
                    }

                    val spannableString = SpannableString(title)
                    queryContent.let {
                        val startIndex = title.indexOf(it, ignoreCase = true)
                        if (startIndex != -1) {
                            val endIndex = startIndex + it.length
                            EaseIM.getContext()?.let {context->
                                spannableString.setSpan(
                                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.ease_color_primary)),
                                    startIndex, endIndex,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }

                    tvName.text = spannableString

                }
            }
        }

        fun searchText(query: String){
            this.queryContent = query
        }

        fun setCheckBoxSelectListener(listener: OnContactSelectedListener){
            this.selectedListener = listener
        }

        private fun isContains(data: MutableList<String>?, username: String): Boolean {
            return data != null && data.contains(username)
        }

    }


}