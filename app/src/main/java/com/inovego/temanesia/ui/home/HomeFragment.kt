package com.inovego.temanesia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.FragmentHomeBinding
import com.inovego.temanesia.helper.ViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdapter
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.actionBarCustom.tvUsername.text = it
        }

        adapter = HomeAdapter { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }
        homeViewModel.listBeasiswa.observe(viewLifecycleOwner) {
            it?.let { data -> adapter.submitList(data) }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBeasiswa.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.cardFeatures.apply {
            cardBeasiswa.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_beasiswaActivity)
            }
            cardLowongan.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_beasiswaActivity)
            }
            cardLomba.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_beasiswaActivity)
            }
            cardSertifikat.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_beasiswaActivity)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}