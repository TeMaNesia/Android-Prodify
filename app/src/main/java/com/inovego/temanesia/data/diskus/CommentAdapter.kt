package com.inovego.temanesia.data.diskus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.ItemCommentBinding
import com.inovego.temanesia.databinding.ItemListDiscussLinearBinding
import java.util.Calendar

class CommentAdapter(uid: String): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var listData = ArrayList<Comment>()

    fun setData(newListData: MutableList<Comment>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCommentBinding.bind(itemView)

        fun bind(data: Comment) {
            binding.namaLengkap.text = data.author_name
            binding.tvComment.text = data.content
            binding.textUpvoteCount.text = data.up_vote.toString()

            Glide.with(itemView.context)
                .load(data.author_img_url)
                .placeholder(R.drawable.avatar_placeholder)
                .into(binding.imgAvatar)

            val timestampCalendar = Calendar.getInstance().apply { time = data.created_at!!.toDate() }
            val currentCalendar = Calendar.getInstance().apply { time = Timestamp.now().toDate() }

            val days = currentCalendar.get(Calendar.DAY_OF_YEAR) - timestampCalendar.get(Calendar.DAY_OF_YEAR)
            val hours = currentCalendar.get(Calendar.HOUR_OF_DAY) - timestampCalendar.get(Calendar.HOUR_OF_DAY)
            val minutes = currentCalendar.get(Calendar.MINUTE) - timestampCalendar.get(Calendar.MINUTE)
            val seconds = currentCalendar.get(Calendar.SECOND) - timestampCalendar.get(Calendar.SECOND)

            if (days > 0) {
                binding.tvTime.text = "$days hari yang lalu"
            } else if (hours > 0) {
                binding.tvTime.text = "$hours jam yang lalu"
            } else if (minutes > 0) {
                binding.tvTime.text = "$minutes menit yang lalu"
            } else {
                binding.tvTime.text = "$seconds detik yang lalu"
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
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