package com.inovego.temanesia.ui.auth.view

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.ItemsTextFieldBinding
import com.inovego.temanesia.utils.CONFIRM_ACCOUNT
import com.inovego.temanesia.utils.EMAIL_PASSWORD
import com.inovego.temanesia.utils.EMAIL_PASSWORD_CONFIRM_PASSWORD
import com.inovego.temanesia.utils.NAME_NIK_ADDRESS
import com.inovego.temanesia.utils.NIM_NISN_SCHOOL_JURUSAN
import com.inovego.temanesia.utils.isEmailValid


class AuthTextInputLinearLayout : LinearLayout {
    private lateinit var binding: ItemsTextFieldBinding

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
        val inflater = LayoutInflater.from(context)
        binding = ItemsTextFieldBinding.inflate(inflater, this)
    }

    private fun hideVisibility(vararg view: AuthTextInput) {
        view.forEach { it.isVisible = false }
    }

    private fun showVisibility(vararg view: AuthTextInput) {
        view.forEach { it.isVisible = true }
    }

    fun getJurusanDropdown(context: Context) {
        val jurusanList = listOf("Informatika", "Mesin", "Listrik", "Sipil", "Grafika")
        val dropDownAdapter = ArrayAdapter(context, R.layout.item_dropdown, jurusanList)
        (binding.autoCompleteJurusan as? AutoCompleteTextView)?.setAdapter(dropDownAdapter)
    }

    fun getView() = binding

    fun checkTextInputAndError(
        session: String,
        isCheckError: Boolean = true,
    ): Boolean {
        binding.apply {
            return when (session) {
                EMAIL_PASSWORD -> {
                    showVisibility(emailTextFieldContainer, passwordTextFieldContainer)
                    val email = emailTextField.checkTextInput(emailTextFieldContainer, isCheckError)
                    val password = passwordTxtField.checkTextInput(
                        passwordTextFieldContainer,
                        isCheckError,
                        isLoginSession = true
                    )

                    email && password
                }

                NIM_NISN_SCHOOL_JURUSAN -> {
                    showVisibility(
                        nimNisnTextFieldContainer,
                        sekolahTextFieldContainer,
                        jurusanTextFieldContainer
                    )
                    val nimNisn = nimNisnTxtField.checkTextInput(
                        nimNisnTextFieldContainer,
                        isCheckError
                    )
                    val sekolah = kampusSekolahTxtField.checkTextInput(
                        sekolahTextFieldContainer,
                        isCheckError
                    )

                    val jurusan = autoCompleteJurusan.checkTextInput(
                        jurusanTextFieldContainer,
                        isCheckError
                    )
                    nimNisn && sekolah && jurusan
                }

                NAME_NIK_ADDRESS -> {
                    hideVisibility(
                        jurusanTextFieldContainer,
                        nimNisnTextFieldContainer,
                        sekolahTextFieldContainer
                    )
                    showVisibility(
                        namaTextFieldContainer,
                        nikTextFieldContainer,
                        alamatTextFieldContainer
                    )

                    val nama = namaLengkapTextField.checkTextInput(
                        namaTextFieldContainer,
                        isCheckError
                    )
                    val nik = nikTextField.checkTextInput(
                        nikTextFieldContainer,
                        isCheckError
                    )

                    val address = alamatTextField.checkTextInput(
                        alamatTextFieldContainer,
                        isCheckError
                    )

                    nama && nik && address
                }

                EMAIL_PASSWORD_CONFIRM_PASSWORD -> {
                    hideVisibility(
                        namaTextFieldContainer,
                        nikTextFieldContainer,
                        alamatTextFieldContainer
                    )
                    showVisibility(
                        emailTextFieldContainer,
                        passwordTextFieldContainer,
                        konfirmasiPasswordTextFieldContainer
                    )
                    val email = emailTextField.checkTextInput(
                        emailTextFieldContainer,
                        isCheckError
                    )

                    val password = passwordTxtField.checkTextInput(
                        passwordTextFieldContainer,
                        isCheckError
                    )
                    val konfirmasiPassword = konfirmasiPasswordTxtField.checkTextInput(
                        konfirmasiPasswordTextFieldContainer,
                        isCheckError,
                    )

                    email && password && konfirmasiPassword
                }

                CONFIRM_ACCOUNT -> {
                    hideVisibility(
                        emailTextFieldContainer,
                        passwordTextFieldContainer,
                        konfirmasiPasswordTextFieldContainer
                    )
                    true
                }

                else -> throw NotFoundException("No Such a $session Session")
            }
        }
    }

    private fun TextInputEditText.checkTextInput(
        textInputLayout: AuthTextInput,
        isCheckError: Boolean,
        message: String? = "Lengkapi Input",
        isLoginSession: Boolean? = false,
    ): Boolean {
        return if (isCheckError && this.text.toString().isEmpty()) {
            textInputLayout.enableErrorAndSetMessage(message)
            false
        } else if (isCheckError) {
            checkTextInputError(textInputLayout, isLoginSession!!)
        } else {
            textInputLayout.setEditTextAndError(this)
            textInputLayout.disableError()
            true
        }
    }

    private fun AutoCompleteTextView.checkTextInput(
        textInputLayout: AuthTextInput,
        isCheckError: Boolean,
        message: String? = "Lengkapi Input",
        isLoginSession: Boolean? = false,
    ): Boolean {
        return if (isCheckError && this.text.toString().isEmpty()) {
            textInputLayout.enableErrorAndSetMessage(message)
            false
        } else if (isCheckError) {
            checkTextInputError(textInputLayout, isLoginSession!!)
        } else {
            textInputLayout.setAutoTextAndError(this)
            textInputLayout.disableError()
            true
        }
    }

    private fun checkTextInputError(errorLayout: AuthTextInput, isLoginSession: Boolean): Boolean {
        return when (errorLayout.id) {
            binding.emailTextFieldContainer.id -> {
                val isEmailValid = binding.emailTextField.text.toString().isEmailValid()
                if (!isEmailValid) errorLayout.enableErrorAndSetMessage("Input Email Yang Benar")
                else errorLayout.disableError()

                isEmailValid
            }

            binding.passwordTextFieldContainer.id -> {
                val isPasswordValid = binding.passwordTxtField.text!!.length >= 6
                val isPasswordTheSame =
                    binding.passwordTxtField.text.toString() == binding.konfirmasiPasswordTxtField.text.toString()

                if (!isPasswordValid) errorLayout.enableErrorAndSetMessage("Password Minimal 6 karakter")
                else {
                    errorLayout.disableError()
                    errorLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }

                return if (isLoginSession) isPasswordValid
                else isPasswordTheSame
            }

            binding.konfirmasiPasswordTextFieldContainer.id -> {
                val isPasswordTheSame =
                    binding.passwordTxtField.text.toString() == binding.konfirmasiPasswordTxtField.text.toString()
                if (!isPasswordTheSame) errorLayout.enableErrorAndSetMessage("Samakan dengan Password")
                else {
                    errorLayout.disableError()
                    errorLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }

                isPasswordTheSame
            }

            else -> {
                errorLayout.disableError()
                true
            }
        }
    }

    private fun AuthTextInput.enableErrorAndSetMessage(message: String?) {
        this.isErrorEnabled = true
        error = message
    }

    private fun AuthTextInput.disableError() {
        this.isErrorEnabled = false
    }
}