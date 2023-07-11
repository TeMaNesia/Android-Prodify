package com.inovego.temanesia.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.FragmentLoginBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.utils.EMAIL_PASSWORD
import com.inovego.temanesia.utils.createToast


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    private val viewModel: AuthViewModel by activityViewModels {
        ViewModelFactory.getInstance(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
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
        viewModel.toastText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { text ->
                createToast(requireContext(), text)
            }
        }

        binding.login.apply {
            authButton.text = getString(R.string.btn_text_login)
            authWelcomeText.text = getString(R.string.text_welcome)
            authWelcomeDescriptionText.text = getString(R.string.text_welcome_description)
            authTextInputLayout.checkTextInputAndError(EMAIL_PASSWORD, false)

            authStatusText.apply {
                titleLoginRegisterText.text = getString(R.string.text_belum_mendaftar)
                loginRegisterText.setOnClickListener {
                    findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
                }
            }

            authButton.setOnClickListener {
                if (authTextInputLayout.checkTextInputAndError(EMAIL_PASSWORD)) {
                    val email = authTextInputLayout.getView().emailTextField.text
                    val password = authTextInputLayout.getView().passwordTxtField.text
                    signInFirebase(email.toString(), password.toString())
                }
            }

//            btnTest.setOnClickListener {
//                viewModel.sendEmailVerification()
//            }
        }
    }

    private fun signInFirebase(email: String, password: String) {
        viewModel.signInFirebase(email, password)
        viewModel.isSignedIn.observe(viewLifecycleOwner) {
            if (it) findNavController().navigate(R.id.action_navigation_login_to_mainActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}