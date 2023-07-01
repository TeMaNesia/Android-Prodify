package com.inovego.temanesia.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.register.authWelcomeText.text = "Register"
        binding.register.apply {
            authButton.setOnClickListener {
                authTextFieldList.apply {
                    val email = emailTxtField.text.toString()
                    val password = passwordTxtField.text.toString()
                    signupFirebase(email, password, password)
                }
            }
        }
    }

    private fun signupFirebase(email: String, password: String, confirmPassword: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password != confirmPassword) {
                Toast.makeText(
                    requireContext(),
                    "Samakan Password dan Konfirmasi Password",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("testing", "${it.result}")
                    findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Lengkapi Input",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}