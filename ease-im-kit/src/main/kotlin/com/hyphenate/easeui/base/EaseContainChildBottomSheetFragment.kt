package com.hyphenate.easeui.base

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.dialog.EaseBottomSheetChildHelper
import com.hyphenate.easeui.common.dialog.EaseBottomSheetContainerHelper
import com.hyphenate.easeui.databinding.EaseFragmentContainTitleBaseBinding
import java.util.Stack

/**
 * Base bottom fragment which has child fragment and title
 */
abstract class EaseContainChildBottomSheetFragment : EaseBaseSheetFragmentDialog<EaseFragmentContainTitleBaseBinding>(),
    EaseBottomSheetContainerHelper {
    protected var currentChild: EaseBottomSheetChildHelper? = null
    protected var childStack: Stack<EaseBottomSheetChildHelper> = Stack<EaseBottomSheetChildHelper>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseFragmentContainTitleBaseBinding? {
        return EaseFragmentContainTitleBaseBinding.inflate(inflater)
    }

    override fun startFragment(fragment: Fragment?, tag: String?) {
        var tag = tag
        require(fragment is EaseBottomSheetChildHelper) { "only BottomSheetChildFragment can be started here " }
        if (TextUtils.isEmpty(tag)) {
            tag = fragment.javaClass.simpleName
        }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, fragment, tag)
            .addToBackStack(null)
            .commit()
        childStack.add(fragment as EaseBottomSheetChildHelper)
        currentChild = fragment
        initTileBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.root?.let { setOnApplyWindowInsets(it) }
    }

    private fun initTileBar() {
        if (!showTitle()) {
            return
        }
        if (currentChild != null) {
            binding?.titleBar?.setTitle(context?.getString(currentChild?.titleBarTitle!!))
            if (currentChild?.isShowTitleBarLeftLayout == true) {
                binding?.titleBar?.setDisplayHomeAsUpEnabled(true)
                binding?.titleBar?.setNavigationIcon(R.drawable.ease_titlebar_back)
            } else {
                binding?.titleBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitleBar()

        // set child fragment
        val childFragment = childFragment
        startFragment(childFragment, childFragment.javaClass.simpleName)
    }

    protected open fun setTitleBar() {}

    override fun initListener() {
        super.initListener()
        binding?.titleBar?.setNavigationOnClickListener {
            back()
        }
    }

    override fun back() {
        if (childFragmentManager.backStackEntryCount > 1) {
            childFragmentManager.popBackStack()
            childStack.pop()
            currentChild = childStack.peek()
            initTileBar()
        } else {
            hide()
        }
    }

    override fun changeNextColor(change: Boolean) {

    }

    protected abstract val childFragment: Fragment

    /**
     * Whether to show titleBar
     * @return
     */
    protected fun showTitle(): Boolean {
        return true
    }
}