package com.inovego.temanesia.ui.auth.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AuthTextInput : TextInputLayout {

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
        error = "Lengkapi Input"
        errorIconDrawable = null
    }

    fun setEditTextAndError(editText: TextInputEditText, errorMessage: String? = "Lengkapi Input") {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkError(p0.toString(), errorMessage)
            }
        })
    }

    fun setAutoTextAndError(autoCompleteTextview: AutoCompleteTextView, errorMessage: String? = "Lengkapi Input") {
        autoCompleteTextview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkError(p0.toString(), errorMessage)
            }
        })
    }

    fun checkError(txt: String, errorMessage: String? = "Lengkapi Input") {
        isErrorEnabled = txt.isEmpty()
        if (isErrorEnabled) error = errorMessage
    }
}