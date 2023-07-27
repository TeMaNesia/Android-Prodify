package com.inovego.temanesia.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.FragmentDiscoverBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.ui.home.HomeViewModel
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
import com.inovego.temanesia.utils.FIREBASE_USER_MOBILE

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val homeViewModel: HomeViewModel by activityViewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val discover = ViewModelProvider(this)[DiscoverViewModel::class.java]
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        discover.text.observe(viewLifecycleOwner) {
            binding.actionBarCustom.tvUsername.text = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getUserData(FIREBASE_USER_MOBILE).observe(viewLifecycleOwner) { jurusan ->
            if (!jurusan.isNullOrEmpty()) {
                when (jurusan) {
                    "Informatika dan Komputer" -> {
                        setRecycleView(jurusan)
                    }

                    "Sipil" -> {

                    }
                }
            }
        }
    }

    private fun setRecycleView(jurusan: String) {
        val adapterLastMinute = HomeAdapter { item ->
            findNavController().navigate(
                R.id.action_navigation_discover_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        getRecycleViewData(FIREBASE_LOWONGAN, jurusan)
        homeViewModel.lowongan.observe(viewLifecycleOwner){
            adapterLastMinute.submitList(it)
        }

        binding.rvLastMinute.apply {
            adapter = adapterLastMinute
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getRecycleViewData(collection: String, jurusan: String) {
        homeViewModel.getListItemByTime(collection, jurusan)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}