package com.inovego.temanesia.lamaran

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.data.model.Lamaran
import com.inovego.temanesia.databinding.ItemListLamaranBinding

class LamaranAdapter :
    ListAdapter<Lamaran, LamaranAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListLamaranBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val profile = getItem(position)
        holder.bind(profile)
    }

    inner class MyViewHolder(private val binding: ItemListLamaranBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(lamaran: Lamaran) {
            binding.apply {
                tvTitleLamaran.text = lamaran.nama_lamaran
                tvStatusPill.text = lamaran.status
                tvDate.text = lamaran.createdAt.toString()
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Lamaran> =
            object : DiffUtil.ItemCallback<Lamaran>() {
                override fun areItemsTheSame(
                    oldItem: Lamaran,
                    newItem: Lamaran,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: Lamaran,
                    newItem: Lamaran,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}