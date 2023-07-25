package com.inovego.temanesia.ui.discuss

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.data.diskus.Diskus
import com.inovego.temanesia.data.diskus.DiskusAdapter
import com.inovego.temanesia.databinding.FragmentDiscussBinding
import com.inovego.temanesia.ui.discover.DiscoverViewModel

class DiscussFragment : Fragment() {

    private lateinit var binding: FragmentDiscussBinding

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val adapter = DiskusAdapter(uid)
    private val diskusList = arrayListOf<Diskus>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDiscussBinding.inflate(layoutInflater)

        binding.btnCreateDiskus.setOnClickListener {
            val intent = Intent(activity, CreateDiscussActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.rvNewDiskus.layoutManager = LinearLayoutManager(context)
        binding.rvNewDiskus.setHasFixedSize(true)
        binding.rvNewDiskus.adapter = adapter

        retreiveAllData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        retreiveAllData()
    }

    private fun retreiveAllData() {
        binding.rvNewDiskus.visibility = View.GONE
        binding.loadingDiskusTerbaru.visibility = View.VISIBLE

        db.collection("diskus")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                diskusList.clear()
                diskusList.addAll(documents.map {
                    val diskus = it.toObject(Diskus::class.java)
                    diskus.id = it.id
                    diskus
                })
                adapter.setData(diskusList)

                binding.loadingDiskusTerbaru.visibility = View.GONE
                binding.rvNewDiskus.visibility = View.VISIBLE
            }
    }
}