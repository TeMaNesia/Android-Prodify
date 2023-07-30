package com.inovego.temanesia.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.ActivityCreateDiscussBinding
import com.inovego.temanesia.databinding.ActivityCreateProfileFeatureBinding

class CreateProfileFeatureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileFeatureBinding
    private lateinit var feature: String

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val uiData = mapOf(
        "pengalaman_kerja" to listOf(
            "Tambah Pengalaman",
            "Posisi",
            "Tulis posisi pekerjaan Anda disini...",
            "Perusahaan",
            "Tulis perusahaan tempat Anda bekerja disini...",
            "Deskripsi",
            "Tulis deskripsi pekerjaan Anda disini...",
        ),
        "pendidikan" to listOf(
            "Tambah Pendidikan",
            "Nama Instansi",
            "Tulis nama instansi pendidikan Anda disini...",
            "Jurusan",
            "Tulis jurusan pendidikan Anda disini...",
            "Deskripsi",
            "Tulis deskripsi pendidikan Anda disini...",
        ),
        "sertifikat" to listOf(
            "Tambah Sertifikat",
            "Nama Sertifikat",
            "Tulis nama sertifikat Anda disini...",
            "ID/Kode",
            "Tulis ID atau kode sertifikat Anda disini...",
            "Deskripsi",
            "Tulis deskripsi sertifikat Anda disini...",
        ),
        "organisasi" to listOf(
            "Tambah Organisasi",
            "Posisi",
            "Tulis posisi Anda pada organisasi disini...",
            "Nama Organisasi",
            "Tulis nama organisasi Anda disini...",
            "Deskripsi",
            "Tulis deskripsi disini...",
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        feature = intent.getStringExtra("FEATURE").toString()

        binding.tvPageTitle.text = uiData[feature]!![0]
        binding.tvTitle.text = uiData[feature]!![1]
        binding.edtTitle.hint = uiData[feature]!![2]
        binding.tvSubTitle.text = uiData[feature]!![3]
        binding.edtSubTitle.hint = uiData[feature]!![4]
        binding.tvDescription.text = uiData[feature]!![5]
        binding.edtDescription.hint = uiData[feature]!![6]


        ArrayAdapter.createFromResource(
            this,
            R.array.indonesian_months,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.edtStartMonth.adapter = adapter
            binding.edtEndMonth.adapter = adapter
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val title = binding.edtTitle.text.toString()
        val subTitle = binding.edtSubTitle.text.toString()
        val description = binding.edtDescription.text.toString()
        val startMonth = binding.edtStartMonth.selectedItem.toString()
        val startYear = binding.edtStartYear.text.toString()
        val endMonth = binding.edtEndMonth.selectedItem.toString()
        val endYear = binding.edtEndYear.text.toString()

        if (title.isEmpty() || subTitle.isEmpty() || description.isEmpty()) {
            BeautifulDialog.build(this)
                .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Harap isi semua kolom",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ERROR)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .hideNegativeButton(true)
                .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_btn_blue, textColor = ContextCompat.getColor(this, R.color.white), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold), shouldIDismissOnClick = true) {
                }
        } else {
            val startDate = if (startMonth.isNotEmpty() || startYear.isNotEmpty()) "$startMonth $startYear".trim() else null
            val endDate = if (endMonth.isNotEmpty() || endYear.isNotEmpty()) "$endMonth $endYear".trim() else null
            val resultDate = if (startDate != null && endDate != null) "$startDate - $endDate" else startDate ?: endDate?: ""

            val newFeature: MutableMap<String, Any> = HashMap()
            newFeature["title"] = title
            newFeature["sub_title"] = subTitle
            newFeature["description"] = description
            newFeature["date"] = resultDate

            db.collection("users_mobile").document(uid).collection(feature).add(newFeature)
                .addOnSuccessListener {
                    BeautifulDialog.build(this)
                        .title("Berhasil", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                        .description("Berhasil menyimpan data!",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                        .type(type= BeautifulDialog.TYPE.SUCCESS)
                        .position(BeautifulDialog.POSITIONS.CENTER)
                        .hideNegativeButton(true)
                        .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_btn_blue, textColor = ContextCompat.getColor(this, R.color.white), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold), shouldIDismissOnClick = true) {
                            finish()
                        }
                }
        }
    }
}