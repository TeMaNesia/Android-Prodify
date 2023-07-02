package com.inovego.temanesia.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.inovego.temanesia.R
import com.inovego.temanesia.catLogAuth
import com.inovego.temanesia.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.authButton.text = getString(R.string.btn_text_login)
        binding.authButton.setOnClickListener {
            binding.authTextFieldList.apply {
                val email = emailTxtField.text.toString()
                val password = passwordTxtField.text.toString()
                signInFirebase(email, password)
            }
        }
        binding.authStatusText.loginRegisterText.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
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
                catLogAuth(it)
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