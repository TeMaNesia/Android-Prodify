package com.inovego.temanesia.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.data.model.ProfileDummy
import com.inovego.temanesia.databinding.ItemListProfileHistoryBinding

class ProfileAdapter :
    ListAdapter<ProfileDummy, ProfileAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListProfileHistoryBinding.inflate(
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

    inner class MyViewHolder(private val binding: ItemListProfileHistoryBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(profile: ProfileDummy) {
            binding.apply {
                tvTitle.text = profile.title
                tvSubTitle.text = profile.company
                tvDescription.text = profile.description
                tvDate.text = profile.date.toString()
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ProfileDummy> =
            object : DiffUtil.ItemCallback<ProfileDummy>() {
                override fun areItemsTheSame(
                    oldItem: ProfileDummy,
                    newItem: ProfileDummy,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: ProfileDummy,
                    newItem: ProfileDummy,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}