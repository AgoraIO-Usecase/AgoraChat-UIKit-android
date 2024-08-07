package com.hyphenate.easeui.menu

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyphenate.easeui.base.EaseBaseSheetFragmentDialog
import com.hyphenate.easeui.databinding.EaseDialogMenuBinding
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.interfaces.OnMenuDismissListener
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem
import java.util.Collections

open class EaseMenuDialog
    : EaseBaseSheetFragmentDialog<EaseDialogMenuBinding>(),
    IMenu, OnItemClickListener {

    private var showCancelButton: Boolean = true
    private val itemModels = ArrayList<EaseMenuItem>()
    private val itemMap: MutableMap<Int?, EaseMenuItem?> = HashMap()
    private var itemClickListener: OnMenuItemClickListener? = null
    private var dismissListener: OnMenuDismissListener? = null

    private val adapter: EaseMenuAdapter by lazy { EaseMenuAdapter() }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseDialogMenuBinding? {
        return EaseDialogMenuBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            rvDialogList.layoutManager = LinearLayoutManager(context)
            rvDialogList.setHasFixedSize(true)
            rvDialogList.adapter = adapter
            adapter.setData(itemModels)
            adapter.setOnItemClickListener(this@EaseMenuDialog)
            btnCancel.setOnClickListener { dismiss() }
            btnCancel.visibility = if (showCancelButton) View.VISIBLE else View.GONE
            menuDivider.visibility = if (showCancelButton) View.VISIBLE else View.GONE
        }
    }

    fun getMenuAdapter(): EaseMenuAdapter {
        return adapter
    }

    /**
     * Set whether to show cancel button.
     */
    fun showCancel(show: Boolean) {
        this.showCancelButton = show
        binding?.let {
            it.btnCancel.visibility = if (showCancelButton) View.VISIBLE else View.GONE
            it.menuDivider.visibility = if (showCancelButton) View.VISIBLE else View.GONE
        }
    }

    override fun clear() {
        itemModels.clear()
        itemMap.clear()
        adapter.notifyDataSetChanged()
    }

    override fun dismissMenu() {
        dismiss()
    }

    override fun setMenuOrder(itemId: Int, order: Int) {
        if (itemMap.containsKey(itemId)) {
            itemMap[itemId]?.run {
                this.order = order
                sortByOrder(itemModels)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun sortByOrder(itemModels: List<EaseMenuItem>) {
        Collections.sort(itemModels) { o1, o2 ->
            val `val`: Int = o1.order - o2.order
            if (`val` > 0) {
                1
            } else if (`val` == 0) {
                0
            } else {
                -1
            }
        }
    }

    override fun registerMenuItem(
        menuId: Int,
        order: Int,
        title: String,
        groupId: Int,
        isVisible: Boolean,
        resourceId: Int,
        titleColor: Int
    ) {
        if (!itemMap.containsKey(menuId) && isVisible) {
            val item = EaseMenuItem(menuId = menuId, order = order,
                title = title, groupId = groupId, isVisible = isVisible,
                resourceId = resourceId, titleColor = titleColor)
            itemMap[menuId] = item
            itemModels.add(item)
            sortByOrder(itemModels)
            adapter.notifyDataSetChanged()
        }
    }

    override fun registerMenus(menuItems: List<EaseMenuItem>) {
        if (menuItems.isEmpty()) return
        menuItems.filter {
                !itemMap.containsKey(it.menuId)
            }
            .filter { it.isVisible }
            .forEach {
                itemMap[it.menuId] = it
                itemModels.add(it)
            }
        sortByOrder(itemModels)
        adapter.notifyDataSetChanged()
    }

    override fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        this.itemClickListener = listener
    }

    override fun setOnMenuDismissListener(listener: OnMenuDismissListener?) {
        this.dismissListener = listener
    }

    override fun onItemClick(view: View?, position: Int) {
        dismiss()
        val item = itemModels[position]
        itemClickListener?.onMenuItemClick(item, position)
    }

    override fun dismiss() {
        super.dismiss()
        dismissListener?.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismissListener?.onDismiss()
    }

    override fun onDestroyView() {
        dismissListener = null
        itemClickListener = null
        super.onDestroyView()
    }

    /**
     * Add top view for EaseMenuDialog.
     */
    fun addTopView(view: View) {
        binding?.llMenuTop?.let {
            it.removeAllViews()
            it.addView(view)
        }
    }

    /**
     * Clear top view for EaseMenuDialog.
     */
    fun clearTopView(){
        binding?.llMenuTop?.removeAllViews()
    }

}