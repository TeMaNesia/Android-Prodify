package com.inovego.temanesia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.FragmentHomeBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.utils.FIREBASE_LOMBA
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
import com.inovego.temanesia.utils.FIREBASE_USER_MOBILE

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var list = arrayListOf<FeatureItem>()
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
        binding.cardFeatures.apply {
            cardBeasiswa.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_beasiswaActivity)
            }
            cardLowongan.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_lowonganActivity)
            }
            cardLomba.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_lombaActivity)
            }
            cardSertifikat.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_sertifikatActivity)
            }
        }
    }

    private fun setRecycleView(jurusan: String) {
        val adapterLomba = HomeAdapter { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        val adapterLowongan = HomeAdapter { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        val adapterBeasiswa = HomeAdapter { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        val adapterSertifikat = HomeAdapter { item ->
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        getRecycleViewData(FIREBASE_LOWONGAN, jurusan)
        homeViewModel.lowongan.observe(viewLifecycleOwner){
            adapterLowongan.submitList(it)
        }

        getRecycleViewData(FIREBASE_LOMBA, jurusan)
        homeViewModel.lomba.observe(viewLifecycleOwner){
            adapterLomba.submitList(it)
        }

        binding.rvLomba.apply {
            adapter = adapterLomba
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvLowongan.apply {
            adapter = adapterLowongan
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvBeasiswa.apply {
            adapter = adapterBeasiswa
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvSertifikat.apply {
            adapter = adapterSertifikat
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getRecycleViewData(collection: String, jurusan: String, ) {
        homeViewModel.getListItemByJurusan(collection, jurusan)
    }

    private fun getLomba(jurusan: String, function: (List<FeatureItem>?) -> Unit) {
        homeViewModel.getListItemByJurusan(FIREBASE_LOMBA, jurusan)
        homeViewModel.lomba.observe(viewLifecycleOwner) {
            function.invoke(it)
        }
    }

//    private fun getLowongan(jurusan: String, function: (List<FeatureItem>) -> Unit) {
//        homeViewModel.getListItemByJurusan(FIREBASE_LOMBA, jurusan)
//        homeViewModel.listData.observe(viewLifecycleOwner) {
//            it?.let { function.invoke(it) }
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}