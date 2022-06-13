package com.su.mediabox.view.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.isInvisible
import com.google.android.material.card.MaterialCardView
import com.su.mediabox.databinding.ViewSearchBarBinding
import com.su.mediabox.util.Util.hideKeyboard
import com.su.mediabox.util.Util.showKeyboard

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {
    private val binding = ViewSearchBarBinding.inflate(LayoutInflater.from(context), this)

    var text: CharSequence
        get() = binding.searchField.text
        set(value) = binding.searchField.setText(value)

    var isEdit: Boolean
        get() = binding.searchField.isEnabled
        set(value) {
            binding.searchField.isEnabled = value
        }

    init {
        useCompatPadding = true
        //清空键
        binding.searchClear.apply {
            addTextChangedListener {
                isInvisible = it.isNullOrEmpty()
            }
            setOnClickListener {
                text = ""
            }
        }
    }

    fun showKeyboard() = binding.searchField.showKeyboard()

    fun hideKeyboard() = binding.searchField.hideKeyboard()

    fun setSelection(index: Int) = binding.searchField.setSelection(index)

    fun setNavIcon(icon: Drawable) {
        binding.searchIcon.setImageDrawable(icon)
    }

    fun setNavListener(listener: View.OnClickListener) {
        binding.searchIcon.setOnClickListener(listener)
    }

    fun setEnterListener(block: (EditText) -> Unit) {
        binding.searchField.setOnEditorActionListener { v, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH)
                block(v as EditText)
            true
        }
    }

    fun addTextChangedListener(
        beforeTextChanged: (
            text: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) -> Unit = { _, _, _, _ -> },
        onTextChanged: (
            text: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) -> Unit = { _, _, _, _ -> },
        afterTextChanged: (text: Editable?) -> Unit = {}
    ): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s)
            }

            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                beforeTextChanged.invoke(text, start, count, after)
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged.invoke(text, start, before, count)
            }
        }
        binding.searchField.addTextChangedListener(textWatcher)

        return textWatcher
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !isEdit
    }
}
