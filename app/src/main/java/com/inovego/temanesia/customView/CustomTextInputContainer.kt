package com.inovego.temanesia.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class CustomTextInputContainer : TextInputLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        if (isErrorEnabled) error = "Lengkapi Input"
    }

    fun setTextChangeListener(editText: TextInputEditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTextError(p0.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTextError(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                setTextError(p0.toString())
            }
        })
    }

    private fun setTextError(text: String, errorMessage: String? = "Lengkapi Input") {
        if (text.isEmpty()) {
            isErrorEnabled = true
            error = errorMessage
        } else {
            isErrorEnabled = false
        }
    }
}