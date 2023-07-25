package com.inovego.temanesia.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.data.model.Dokumen
import com.inovego.temanesia.databinding.ItemListDokumenBinding

class DetailAdapter(private val onClick: (Dokumen) -> Unit) :
    ListAdapter<Dokumen, DetailAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListDokumenBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemPosition = getItem(position)
        holder.bind(itemPosition)
    }

    inner class MyViewHolder(val binding: ItemListDokumenBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(dokumen: Dokumen) {
            binding.apply {
                tvDokumenTitle.text = dokumen.namaFile
//                tvDokumenDescription.text = dokumen.urlFile
            }
            binding.root.setOnClickListener {
                onClick(dokumen)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Dokumen> =
            object : DiffUtil.ItemCallback<Dokumen>() {
                override fun areItemsTheSame(
                    oldListItem: Dokumen,
                    newListItem: Dokumen,
                ): Boolean {
                    return oldListItem.urlFile == newListItem.urlFile
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldListItem: Dokumen,
                    newListItem: Dokumen,
                ): Boolean {
                    return oldListItem == newListItem
                }
            }
    }
}