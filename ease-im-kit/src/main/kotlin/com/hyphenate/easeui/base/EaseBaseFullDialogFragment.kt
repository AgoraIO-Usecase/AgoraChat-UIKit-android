package com.hyphenate.easeui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.hyphenate.easeui.databinding.EaseFragmentDialogFullBinding

abstract class EaseBaseFullDialogFragment: DialogFragment() {

    lateinit var binding: EaseFragmentDialogFullBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EaseFragmentDialogFullBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Light)
        // Make the dialog full-screen
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachWithChild(childFragmentManager, binding.flFragment)
        initView()
        initListener()
        initData()
    }

    abstract fun attachWithChild(
        childFragmentManager: FragmentManager,
        fragmentContainer: FrameLayout
    )

    open fun initView() {}

    open fun initListener() {}

    open fun initData() {}

}