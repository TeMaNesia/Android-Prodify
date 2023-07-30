package com.inovego.temanesia.ui.detail

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.inovego.temanesia.R
import com.inovego.temanesia.data.diskus.Comment
import com.inovego.temanesia.data.diskus.CommentAdapter
import com.inovego.temanesia.data.diskus.Diskus
import com.inovego.temanesia.data.diskus.DiskusAdapter
import com.inovego.temanesia.databinding.ActivityDetailDiscussBinding
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.round

class DetailDiscussActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDiscussBinding
    private lateinit var docId: String

    private lateinit var uName: String
    private lateinit var uImgUrl: String

    private var upVote = 0

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val commentAdapter = CommentAdapter(uid)
    private val commentList = arrayListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDiscussBinding.inflate(layoutInflater)
        setContentView(binding.root)

        docId = intent.getStringExtra("DOC_ID").toString()

        binding.rvComment.adapter = commentAdapter

        populateView()
        populateComment()

        binding.btnBack.setOnClickListener {
            finish()
        }

        db.collection("users_mobile").document(uid).get()
            .addOnSuccessListener {
                uName = it.getString("nama")!!
                uImgUrl = it.getString("author_img_url")?: ""
            }

        binding.btnSend.setOnClickListener {
            val comment = binding.edtComment.text.toString()
            if (comment.isNotEmpty()) {
                sendComment(comment)
            }
        }

        binding.btnUpvote.setOnClickListener {
            binding.btnUpvote.setImageResource(R.drawable.ic_up_vote_active)
            binding.textUpvoteCount.text = (upVote + 1).toString()
            binding.textUpvoteCount.setTextColor(ContextCompat.getColor(this, R.color.blue100))

            binding.btnDownvote.setImageResource(R.drawable.ic_down_vote)
        }

        binding.btnDownvote.setOnClickListener {
            binding.btnDownvote.setImageResource(R.drawable.ic_down_vote_active)

            binding.btnUpvote.setImageResource(R.drawable.ic_up_vote)
            binding.textUpvoteCount.text = (upVote).toString()
            binding.textUpvoteCount.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
        }
    }

    private fun populateView() {
        binding.loading.visibility = View.VISIBLE

        db.collection("diskus").document(docId).get()
            .addOnSuccessListener {
                val data = it.toObject(Diskus::class.java)

                if (data != null) {
                    binding.tvTitle.text = data.title
                    binding.tvDescription.text = data.content
                    binding.namaLengkap.text = data.author_name
                    binding.textUpvoteCount.text = data.up_vote.toString()
                    binding.textCommentCount.text = data.total_comment.toString()

                    upVote = data.up_vote

                    Glide.with(this)
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
                        val textView: TextView = layoutInflater.inflate(R.layout.item_diskus_tag, binding.flexboxTag, false) as TextView
                        textView.text = "#$tag"
                        binding.flexboxTag.addView(textView)
                    }
                }

                binding.loading.visibility = View.GONE

            }
    }

    private fun populateComment() {
        binding.rvComment.visibility = View.GONE
        binding.loadingComment.visibility = View.VISIBLE

        db.collection("diskus").document(docId).collection("comments")
            .orderBy("created_at", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                commentList.clear()
                commentList.addAll(documents.map {
                    val comment = it.toObject(Comment::class.java)
                    comment.id = it.id
                    comment
                })
                commentAdapter.setData(commentList)

                binding.loadingComment.visibility = View.GONE
                binding.rvComment.visibility = View.VISIBLE

                binding.nestedScrollView.post {
                    binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
    }

    private fun sendComment(commentContent: String) {
        binding.rvComment.visibility = View.GONE
        binding.loadingComment.visibility = View.VISIBLE

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        val createdAt = Timestamp(Calendar.getInstance().time)

        val comment: MutableMap<String, Any> = HashMap()
        comment["content"] = commentContent
        comment["author_name"] = uName
        comment["author_img_url"] = uImgUrl
        comment["uid"] = uid
        comment["created_at"] = createdAt
        comment["up_vote"] = 0
        comment["down_vote"] = 0

        val commentRef = db.collection("diskus").document(docId).collection("comments")

        commentRef.add(comment)
            .addOnSuccessListener {
                binding.edtComment.setText("")
                populateComment()
                binding.nestedScrollView.post {
                    binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
                }

                val parentDocRef = commentRef.parent

                parentDocRef?.get()?.addOnSuccessListener { parent ->
                    parentDocRef.update("total_comment", parent.get("total_comment").toString().toInt()+1)
                }
            }
    }
}