package com.inovego.temanesia.ui.discuss

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.ActivityCreateDiscussBinding
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import java.util.Arrays
import java.util.Calendar

class CreateDiscussActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateDiscussBinding

    private val tagList = mutableListOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDiscussBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUpload.setOnClickListener {
            val title = binding.edtTitle.text.toString()
            val content = binding.edtContent.text.toString()

            if (tagList.size == 0 || title.isEmpty() || content.isEmpty()) {
                BeautifulDialog.build(this)
                    .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                    .description("Harap isi semua kolom",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                    .type(type= BeautifulDialog.TYPE.ERROR)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .hideNegativeButton(true)
                    .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_btn_blue, textColor = ContextCompat.getColor(this, R.color.white), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold), shouldIDismissOnClick = true) {
                    }
            } else {
                db.collection("users_mobile").document(uid).get()
                    .addOnSuccessListener {
                        saveDiskus(title, content, it.getString("nama")!!, it.getString("author_img_url")?: "")
                    }
            }
        }

        val inflater = LayoutInflater.from(this)

        binding.btnAddTag.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.input_tag_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT

            val positiveButton = dialog.findViewById(R.id.btn_save) as TextView
            val negativeButton = dialog.findViewById(R.id.btn_cancel) as TextView

            positiveButton.setOnClickListener {
                val edtTag = dialog.findViewById(R.id.edt_tag) as EditText
                val tag = edtTag.text.toString()
                val textView: TextView = inflater.inflate(R.layout.item_diskus_tag, binding.flexboxTag, false) as TextView

                textView.text = "#$tag"
                binding.flexboxTag.addView(textView)

                tagList.add(tag)

                dialog.dismiss()
            }

            negativeButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            val window = dialog.window
            window!!.attributes = lp
        }
    }

    private fun saveDiskus(title: String, content: String, uName: String, uImgUrl: String) {
        val createdAt = Timestamp(Calendar.getInstance().time)

        val diskus: MutableMap<String, Any> = HashMap()
        diskus["tag"] = tagList
        diskus["title"] = title
        diskus["content"] = content
        diskus["created_at"] = createdAt
        diskus["uid"] = uid
        diskus["author_name"] = uName
        diskus["author_img_url"] = uImgUrl
        diskus["up_vote"] = 0
        diskus["down_vote"] = 0
        diskus["total_comment"] = 0

        db.collection("diskus").add(diskus)
            .addOnSuccessListener {
                BeautifulDialog.build(this)
                    .title("Berhasil", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                    .description("Diskus kamu berhasil dipublish!",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                    .type(type= BeautifulDialog.TYPE.SUCCESS)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .hideNegativeButton(true)
                    .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_btn_blue, textColor = ContextCompat.getColor(this, R.color.white), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold), shouldIDismissOnClick = true) {
                        finish()
                    }
            }
    }
}