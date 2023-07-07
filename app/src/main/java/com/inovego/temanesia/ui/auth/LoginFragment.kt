package com.inovego.temanesia.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.FragmentLoginBinding
import com.inovego.temanesia.helper.ViewModelFactory


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    private val viewModel: AuthViewModel by activityViewModels {
        ViewModelFactory.getInstance()
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.login.apply {
            authButton.text = getString(R.string.btn_text_login)
            authWelcomeText.text = getString(R.string.text_welcome)
            authWelcomeDescriptionText.text = getString(R.string.text_welcome_description)

            authStatusText.apply {
                titleLoginRegisterText.text = getString(R.string.text_belum_mendaftar)
                loginRegisterText.setOnClickListener {
                    findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
                }
            }

            authTextFieldList.apply {
                authButton.setOnClickListener {
                    val email = emailTextField.text.toString()
                    val password = passwordTxtField.text.toString()

                    signInFirebase(email, password)
                }

                with(View.VISIBLE) {
                    emailTextFieldContainer.visibility = this
                    passwordTextFieldContainer.visibility = this
                }
            }
        }
    }

    private fun signInFirebase(email: String, password: String) {
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(
                requireActivity(),
                "Lengkapi Input",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.setUID(it.result.user?.uid.toString())
                findNavController().navigate(R.id.action_navigation_login_to_mainActivity)
                requireActivity().finish()
            } else {
                Toast.makeText(
                    requireActivity(),
                    it.exception?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}