package com.inovego.temanesia.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.ProfileDummy
import com.inovego.temanesia.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val profileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]
    }

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel.text.observe(viewLifecycleOwner) {
            binding.actionBarCustom.tvNamaLengkap.text = it
        }
        setupList { dummy ->
            val adapter = ProfileAdapter()
            adapter.submitList(dummy)
            binding.rvPengalamanKerja.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                this.adapter = adapter
            }

            binding.rvPendidikan.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                this.adapter = adapter
            }

            binding.rvSertifikat.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                this.adapter = adapter
            }

            binding.rvOrganisasi.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                this.adapter = adapter
            }
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

    private fun setupList(function: (List<ProfileDummy>) -> Unit) {
        profileViewModel.getData().observe(viewLifecycleOwner) { dummies ->
            function.invoke(dummies)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}