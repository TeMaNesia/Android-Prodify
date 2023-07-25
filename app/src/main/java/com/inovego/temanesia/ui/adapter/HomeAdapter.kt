package com.inovego.temanesia.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.data.model.ListItem
import com.inovego.temanesia.databinding.ItemListFeatureLinearBinding
import com.inovego.temanesia.utils.loadImageFromUrl

class HomeAdapter(private val onClick: (ListItem) -> Unit) :
    ListAdapter<ListItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

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
        fun bind(listItem: ListItem) {
            binding.apply {
                ivFeatures.loadImageFromUrl(listItem.urlPoster)
                tvPillFeatures.text = listItem.jenisKegiatan
                tvPillLembaga.text = listItem.penyelenggara
                tvFeaturesTitle.text = listItem.nama
                tvFeaturesDescriptionSingkat.text = listItem.ringkasan
                tvDate.text = listItem.date.toString()
            }
            binding.root.setOnClickListener {
                onClick(listItem)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListItem> =
            object : DiffUtil.ItemCallback<ListItem>() {
                override fun areItemsTheSame(
                    oldListItem: ListItem,
                    newListItem: ListItem,
                ): Boolean {
                    return oldListItem.deskripsi == newListItem.deskripsi
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldListItem: ListItem,
                    newListItem: ListItem,
                ): Boolean {
                    return oldListItem == newListItem
                }
            }
    }
}


