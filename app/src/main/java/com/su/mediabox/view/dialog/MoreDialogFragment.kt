package com.su.mediabox.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.su.mediabox.R
import com.su.mediabox.databinding.FragmentMoreDialogBinding

open class MoreDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMoreDialogBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var listeners: Array<View.OnClickListener>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoreDialogBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.run {
            tvCancelMore.setOnClickListener(listeners[0])
            tvDlna.setOnClickListener(listeners[1])
            tvOpenInOtherPlayer.setOnClickListener(listeners[2])
        }
    }

    fun setOnClickListener(listeners: Array<View.OnClickListener>): BottomSheetDialogFragment {
        this.listeners = listeners
        return this
    }
}