package com.inovego.temanesia.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.FragmentDiscoverBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.utils.FIREBASE_BEASISWA
import com.inovego.temanesia.utils.FIREBASE_LOMBA
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
import com.inovego.temanesia.utils.FIREBASE_SERTIFIKASI
import com.inovego.temanesia.utils.FIREBASE_USER_MOBILE

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val discoverViewModel: DiscoverViewModel by activityViewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        discoverViewModel.text.observe(viewLifecycleOwner) {
            binding.actionBarCustom.tvUsername.text = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discoverViewModel.getUserData(FIREBASE_USER_MOBILE).observe(viewLifecycleOwner) { jurusan ->
            if (!jurusan.isNullOrEmpty()) {
                setRecycleView(jurusan, "")
                binding.actionBarCustom.apply {
                    search.setOnQueryTextListener(object :
                        androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            setRecycleView(jurusan, query)
                            hideKeyboard()
                            return true
                        }

                        override fun onQueryTextChange(newText: String): Boolean {
                            if (newText.isEmpty()) setRecycleView(jurusan, "")
                            return true
                        }
                    })
                }
            }

        }
    }

    private fun setRecycleView(jurusan: String, query: String) {
        val adapterLastMinute = HomeAdapter(requireContext()) { item ->
            findNavController().navigate(
                R.id.action_navigation_discover_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        val adapterForYourPage = HomeAdapter(requireContext()) { item ->
            findNavController().navigate(
                R.id.action_navigation_discover_to_navigation_detailFeatureActivity,
                bundleOf("FeatureItem" to item)
            )
        }

        getRecycleViewList(FIREBASE_LOMBA, jurusan)
        getRecycleViewList(FIREBASE_BEASISWA, jurusan)
        getRecycleViewList(FIREBASE_LOWONGAN, jurusan)
        getRecycleViewList(FIREBASE_SERTIFIKASI, jurusan)


        binding.apply {
            shimmerLayoutA.startShimmer()
            shimmerLayoutB.startShimmer()
        }

        getMergedList { list ->
            adapterForYourPage.submitList(list)

            val sortedList = list.sortedBy { it.date }.filter { data ->
                data.nama.lowercase().contains(query)
            }

            adapterLastMinute.submitList(sortedList)

            binding.apply {
                shimmerLayoutA.stopShimmer()
                shimmerLayoutB.stopShimmer()

                shimmerLayoutA.visibility = View.GONE
                shimmerLayoutB.visibility = View.GONE
            }
        }

        binding.rvLastMinute.apply {
            adapter = adapterLastMinute
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvFyp.apply {
            adapter = adapterForYourPage
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getRecycleViewList(collection: String, jurusan: String) {
        discoverViewModel.getListItemByJurusan(collection, jurusan)
    }

    private fun getMergedList(callback: (List<FeatureItem>) -> Unit) {
        val mergedList = mutableListOf<FeatureItem>()
        discoverViewModel.apply {
            lomba.observe(viewLifecycleOwner) { lombaList ->
                lowongan.observe(viewLifecycleOwner) { lowonganList ->
                    beasiswa.observe(viewLifecycleOwner) { beasiswaList ->
                        sertifikasi.observe(viewLifecycleOwner) { sertifikasiList ->
                            // Clear the mergedList before merging the data
                            mergedList.clear()

                            // Add all the data from different sources to the mergedList
                            if (lombaList != null && lowonganList != null && beasiswaList != null && sertifikasiList != null) {
                                mergedList.addAll(lombaList)
                                mergedList.addAll(lowonganList)
                                mergedList.addAll(beasiswaList)
                                mergedList.addAll(sertifikasiList)

                                callback.invoke(mergedList)

                            }
                        }
                    }
                }
            }
        }


    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}