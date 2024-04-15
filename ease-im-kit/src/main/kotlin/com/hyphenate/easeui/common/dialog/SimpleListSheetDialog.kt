package com.hyphenate.easeui.common.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseSheetFragmentDialog
import com.hyphenate.easeui.common.extensions.dpToIntPx
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.databinding.EaseLayoutSimpleSheetDialogBinding
import com.hyphenate.easeui.interfaces.SimpleListSheetItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem

class SimpleListSheetDialog(
    context: Context,
    itemList: MutableList<EaseMenuItem>?,
    val itemListener: SimpleListSheetItemClickListener ?= null,
    val type: SimpleSheetType = SimpleSheetType.ITEM_LAYOUT_DIRECTION_CENTER
):EaseBaseSheetFragmentDialog<EaseLayoutSimpleSheetDialogBinding>() {
    private var mContext:Context
    private var sheetAdapter: SimpleSheetAdapter? = null
    private var data:MutableList<EaseMenuItem>? = null
    private var listener:SimpleListSheetItemClickListener?=null

    init {
        this.mContext = context
        this.data = itemList
        this.listener = itemListener
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseLayoutSimpleSheetDialogBinding {
        return EaseLayoutSimpleSheetDialogBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.root?.let { setOnApplyWindowInsets(it) }
    }

    override fun initView(){
        binding?.let {
            if (type == SimpleSheetType.ITEM_LAYOUT_DIRECTION_START){
                it.cancel.text = ""
                it.cancel.isEnabled = false
                val layoutParams = it.cancel.layoutParams
                layoutParams.height = 34f.dpToIntPx(mContext)
                it.cancel.layoutParams = layoutParams
            }
            sheetAdapter = SimpleSheetAdapter(data,type)
            val layoutManager = LinearLayoutManager(mContext)
            it.rlSheetList.layoutManager = layoutManager
            it.rlSheetList.adapter = this.sheetAdapter
        }
    }

    override fun initListener(){
        sheetAdapter?.setSimpleListSheetItemClickListener(object : SimpleListSheetItemClickListener{
            override fun onItemClickListener(position: Int, menu: EaseMenuItem) {
                listener?.onItemClickListener(position,menu)
            }
        })
        binding?.cancel?.setOnClickListener{
            dismiss()
        }
    }

    fun setSimpleListSheetItemClickListener(listener: SimpleListSheetItemClickListener?) {
        this.listener = listener
    }

}



class SimpleSheetAdapter(
    private val dataList: MutableList<EaseMenuItem>?,
    private val type: SimpleSheetType,
):RecyclerView.Adapter<SimpleSheetAdapter.ViewHolder>() {
    private lateinit var listener: SimpleListSheetItemClickListener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.itemContent)
        val dividerView:View = itemView.findViewById(R.id.view_divider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ease_simple_sheet_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return dataList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataList?.let { it ->
            val item = it[position]
            if (item.isVisible){
                holder.textView.visibility = View.VISIBLE
                holder.dividerView.visibility = View.VISIBLE
            }else{
                holder.textView.visibility = View.GONE
                holder.dividerView.visibility = View.GONE
            }
            if (type == SimpleSheetType.ITEM_LAYOUT_DIRECTION_START){
                val context = holder.itemView.context
                holder.textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                item.resourceId.let { res->
                    val drawable = context.resources.getDrawable(res)
                    drawable.setBounds(0, 0, 24.dpToPx(context), 24.dpToPx(context))
                    val paddingLeft = context.resources.getDimensionPixelSize(R.dimen.ease_size_4)
                    holder.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    holder.textView.compoundDrawablePadding = paddingLeft
                }
                item.resourceTintColor.let { color->
                    if (color != -1){
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            holder.textView.compoundDrawableTintList = ColorStateList.valueOf(color)
                        } else {
                            holder.textView.compoundDrawables?.forEach { d ->
                                d.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                            }
                        }
                    }
                }
            }
            holder.textView.text = item.title
            holder.textView.setTextColor(item.titleColor)

            holder.textView.setOnClickListener{
                listener.onItemClickListener(position,item)
            }
        }
    }

    fun setSimpleListSheetItemClickListener(listener: SimpleListSheetItemClickListener){
        this.listener = listener
    }

}

enum class SimpleSheetType(var code: Int){
    ITEM_LAYOUT_DIRECTION_START(0),
    ITEM_LAYOUT_DIRECTION_CENTER(1)
}