package com.inovego.temanesia.repoSertif

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.R
import com.inovego.temanesia.data.profile.ProfileFeature
import com.inovego.temanesia.data.profile.ProfileFeatureAdapter
import com.inovego.temanesia.databinding.ItemListProfileHistoryBinding
import com.inovego.temanesia.databinding.ItemListSertifikatBinding

class CertificateRepositoyAdapter(private val context: Context): RecyclerView.Adapter<CertificateRepositoyAdapter.ViewHolder>() {

    private var listData = ArrayList<Certificate>()

    fun setData(newListData: MutableList<Certificate>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListSertifikatBinding.bind(itemView)

        fun bind(data: Certificate) {
            binding.tvTitleSertifikat.text = data.nama
            binding.tvProgram.text = data.nama_lomba
            binding.tvId.text = data.nomor

            binding.btnUnduh.setOnClickListener {
                val openUrl = Intent(Intent.ACTION_VIEW)
                openUrl.data = Uri.parse(data.file_sertifikat)
                context.startActivity(openUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_sertifikat, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }
}
