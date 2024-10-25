package io.agora.uikit.feature.chat.pin

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.dpToPx
import io.agora.uikit.feature.chat.adapter.EaseChatPinMessageListAdapter
import io.agora.uikit.interfaces.OnItemClickListener

class EaseChatPinMessageListViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    context, attrs, defStyleAttr
){
    private var constraintLayout: ConstraintLayout? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: EaseChatPinMessageListAdapter? = null
    private var itemClickListener: OnPinItemClickListener? = null
    private var viewStatusChangeListener:OnPinViewStatusChangListener? = null
    private var tvCount: TextView? = null
    private var clBottom: View? = null
    private var itemSubViewClickListener: EaseBaseRecyclerViewAdapter.OnItemSubViewClickListener? = null

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        isClickable = true
        setBackgroundColor(Color.parseColor("#80000000"))
        constraintLayout = LayoutInflater.from(getContext())
            .inflate(R.layout.ease_pin_message_list_view_group, this, false) as ConstraintLayout
        addView(constraintLayout)
        tvCount = findViewById<TextView>(R.id.tv_count)
        recyclerView = findViewById(R.id.rv_list)
        clBottom = findViewById<View>(R.id.cl_bottom)

        adapter = EaseChatPinMessageListAdapter()
        val space = 8.dpToPx(context)
        val itemDecoration = EaseChatPinItemSpaceDecoration(space)
        recyclerView?.addItemDecoration(itemDecoration)
        recyclerView?.layoutManager = LinearLayoutManager(getContext())
        adapter?.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                itemClickListener?.onItemClick(adapter?.getItem(position))
            }

        })
        adapter?.setOnItemSubViewClickListener(object : EaseBaseRecyclerViewAdapter.OnItemSubViewClickListener{
                override fun onItemSubViewClick(view: View?, position: Int) {
                    itemSubViewClickListener?.onItemSubViewClick(view, position)
                }
            })
        recyclerView?.adapter = adapter

    }

    var startY: Long = 0
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startY = event.y.toLong()
            MotionEvent.ACTION_UP -> {
                recyclerView?.let {
                    if (!it.canScrollVertically(-1) && startY - event.y > 20) {
                        visibility = GONE
                        viewStatusChangeListener?.onHidePinView()
                        return true
                    }
                    if (event.x < 0 || event.x > width || event.y < 0 || event.y > height || event.y > clBottom?.top!!) {
                        visibility = GONE
                        viewStatusChangeListener?.onHidePinView()
                        return true
                    }
                }

            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun setData(data: MutableList<ChatMessage>?) {
        data?.let {
            tvCount?.text = "${it.size} Pin Message"
            adapter?.setData(it)
        }
    }

    fun setOnItemClickListener(listener: OnPinItemClickListener?) {
        itemClickListener = listener
    }

    fun setOnItemSubViewClickListener(listener: EaseBaseRecyclerViewAdapter.OnItemSubViewClickListener?) {
        itemSubViewClickListener = listener
    }

    fun setPinViewStatusChangeListener(listener:OnPinViewStatusChangListener?){
        viewStatusChangeListener = listener
    }

    fun show(messages: MutableList<ChatMessage>?) {
        visibility = VISIBLE
        setData(messages)
    }

    fun removeData(message: ChatMessage?) {
        adapter?.let {
            val messageList: MutableList<ChatMessage>? = it.data?.toMutableList()
            if (messageList != null && message != null) {
                for (i in messageList.indices) {
                    if (messageList[i].msgId.equals(message.msgId)) {
                        messageList.remove(message)
                        break
                    }
                }
                it.notifyDataSetChanged()
            }
            if (it.data.isNullOrEmpty()) {
                visibility = GONE
                viewStatusChangeListener?.onHidePinView()
            }
        }
    }

    fun addData(message: ChatMessage){
        adapter?.addData(0,message)
    }

    fun setConstraintLayoutMaxHeight(height: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val recyclerViewId: Int = R.id.rv_list
        val rvHeight = height - tvCount!!.height - clBottom!!.height
        constraintSet.constrainMaxHeight(recyclerViewId, rvHeight)
        constraintSet.applyTo(constraintLayout)
    }

    interface OnPinItemClickListener {
        fun onItemClick(message: ChatMessage?)
    }

    interface OnPinViewStatusChangListener{
        fun onHidePinView(){}
    }

}