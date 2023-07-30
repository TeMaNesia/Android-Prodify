package com.inovego.temanesia.data.diskus

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.ItemListDiscussLinearBinding
import com.inovego.temanesia.ui.detail.DetailDiscussActivity
import java.time.Duration
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.round

class DiskusAdapter(private val uid: String): RecyclerView.Adapter<DiskusAdapter.ViewHolder>() {

    private val db = Firebase.firestore
    private var listData = ArrayList<Diskus>()

    fun setData(newListData: MutableList<Diskus>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListDiscussLinearBinding.bind(itemView)

        fun bind(data: Diskus) {
            binding.tvTitle.text = data.title
            binding.tvDescription.text = data.content
            binding.namaLengkap.text = data.author_name
            binding.textUpvoteCount.text = data.up_vote.toString()
            binding.textCommentCount.text = data.total_comment.toString()

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

            binding.flexboxTag.removeAllViews()

            for (tag in data.tag) {
                val textView: TextView = LayoutInflater.from(itemView.context).inflate(R.layout.item_diskus_tag, binding.flexboxTag, false) as TextView
                textView.text = "#$tag"
                binding.flexboxTag.addView(textView)
            }

            binding.btnUpvote.setOnClickListener {
                binding.btnUpvote.setImageResource(R.drawable.ic_up_vote_active)
                binding.textUpvoteCount.text = (data.up_vote + 1).toString()
                binding.textUpvoteCount.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue100))

                binding.btnDownvote.setImageResource(R.drawable.ic_down_vote)

                binding.btnUpvote.isClickable = false
            }

            binding.btnDownvote.setOnClickListener {
                binding.btnDownvote.setImageResource(R.drawable.ic_down_vote_active)

                binding.btnUpvote.setImageResource(R.drawable.ic_up_vote)
                binding.textUpvoteCount.text = (data.up_vote).toString()
                binding.textUpvoteCount.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_gray))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_discuss_linear, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)

        holder.itemView.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val intent = Intent(activity, DetailDiscussActivity::class.java)
            activity.startActivity(intent.apply {
                putExtra("DOC_ID", data.id)
            })
        }
    }
}