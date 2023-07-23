package com.inovego.temanesia.ui.discuss

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inovego.temanesia.databinding.FragmentDiscussBinding
import com.inovego.temanesia.ui.discover.DiscoverViewModel

class DiscussFragment : Fragment() {

    private var _binding: FragmentDiscussBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val discussViewModel = ViewModelProvider(this)[DiscussViewModel::class.java]
        _binding = FragmentDiscussBinding.inflate(inflater, container, false)

        discussViewModel.text.observe(viewLifecycleOwner) {
            binding.actionBarCustom.tvUsername.text = it
        }

        binding.btnCreateDiskus.setOnClickListener {
            val intent = Intent(activity, CreateDiscussActivity::class.java)
            activity?.startActivity(intent)
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}