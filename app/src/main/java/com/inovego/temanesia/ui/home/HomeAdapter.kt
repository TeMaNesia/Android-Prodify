package com.inovego.temanesia.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.data.model.BeasiswaItem
import com.inovego.temanesia.databinding.ItemListFeatureLinearBinding
import com.inovego.temanesia.utils.loadImageFromUrl

class HomeAdapter(private val onClick: (BeasiswaItem) -> Unit) :
    ListAdapter<BeasiswaItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListFeatureLinearBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemPosition = getItem(position)
        holder.bind(itemPosition)
    }

    inner class MyViewHolder(val binding: ItemListFeatureLinearBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item: BeasiswaItem) {
            binding.apply {
                ivFeatures.loadImageFromUrl(item.urlPoster)
                tvPillFeatures.text = item.jenisKegiatan
                tvPillLembaga.text = item.penyelenggara
                tvFeaturesTitle.text = item.nama
                tvFeaturesDescriptionSingkat.text = item.ringkasan
                tvDate.text = item.date.toString()
            }
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<BeasiswaItem> =
            object : DiffUtil.ItemCallback<BeasiswaItem>() {
                override fun areItemsTheSame(
                    oldItem: BeasiswaItem,
                    newItem: BeasiswaItem,
                ): Boolean {
                    return oldItem.deskripsi == newItem.deskripsi
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: BeasiswaItem,
                    newItem: BeasiswaItem,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}


