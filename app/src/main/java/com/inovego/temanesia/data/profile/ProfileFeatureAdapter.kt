package com.inovego.temanesia.data.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.R
import com.inovego.temanesia.data.diskus.Comment
import com.inovego.temanesia.data.diskus.CommentAdapter
import com.inovego.temanesia.databinding.ItemListProfileHistoryBinding

class ProfileFeatureAdapter(): RecyclerView.Adapter<ProfileFeatureAdapter.ViewHolder>() {

    private var listData = ArrayList<ProfileFeature>()

    fun setData(newListData: MutableList<ProfileFeature>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListProfileHistoryBinding.bind(itemView)

        fun bind(data: ProfileFeature) {
            binding.tvTitle.text = data.title
            binding.tvSubTitle.text = data.sub_title
            binding.tvDescription.text = data.description
            binding.tvDate.text = data.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_profile_history, parent, false)
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