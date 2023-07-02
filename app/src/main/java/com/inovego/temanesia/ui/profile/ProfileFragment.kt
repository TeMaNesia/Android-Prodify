package com.inovego.temanesia.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel.text.observe(viewLifecycleOwner) {
            binding.actionBarCustom.tvNamaLengkap.text = it
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actionBarCustom.icProfileSettingContainer.setOnClickListener {
            firebaseAuth.signOut()
            findNavController().navigate(R.id.action_navigation_profile_to_authActivity)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}