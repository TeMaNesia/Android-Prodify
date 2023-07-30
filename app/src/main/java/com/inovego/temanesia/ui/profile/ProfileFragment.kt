package com.inovego.temanesia.ui.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.inovego.temanesia.R
import com.inovego.temanesia.data.profile.ProfileFeature
import com.inovego.temanesia.data.profile.ProfileFeatureAdapter
import com.inovego.temanesia.databinding.FragmentProfileBinding
import com.inovego.temanesia.lamaran.LamaranStatusActivity
import com.inovego.temanesia.repoSertif.CertificateRepositoryActivity
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db = FirebaseFirestore.getInstance()

    private var profileSummary = ""

    private val pengalamanKerjaAdapter = ProfileFeatureAdapter()
    private val pengalamanKerjaList = arrayListOf<ProfileFeature>()

    private val pendidikanAdapter = ProfileFeatureAdapter()
    private val pendidikanList = arrayListOf<ProfileFeature>()

    private val sertifikatAdapter = ProfileFeatureAdapter()
    private val sertifikatList = arrayListOf<ProfileFeature>()

    private val organisasiAdapter = ProfileFeatureAdapter()
    private val organisasiList = arrayListOf<ProfileFeature>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.rvPengalamanKerja.apply {
            adapter = pengalamanKerjaAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvPendidikan.apply {
            adapter = pendidikanAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvSertifikat.apply {
            adapter = sertifikatAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        binding.rvOrganisasi.apply {
            adapter = organisasiAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        refreshData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionBarCustom.icProfileSettingContainer.setOnClickListener {
            showSettingPopup(binding.actionBarCustom.icProfileSettingContainer)
        }

        binding.cvTentangSaya.setOnClickListener {
            showSummaryDialog()
        }

        binding.cvPengalamanKerja.setOnClickListener {
            val intent = Intent(activity, CreateProfileFeatureActivity::class.java).apply {
                putExtra("FEATURE", "pengalaman_kerja")
            }
            activity?.startActivity(intent)
        }

        binding.cvPendidikan.setOnClickListener {
            val intent = Intent(activity, CreateProfileFeatureActivity::class.java).apply {
                putExtra("FEATURE", "pendidikan")
            }
            activity?.startActivity(intent)
        }

        binding.cvSertifikat.setOnClickListener {
            val intent = Intent(activity, CreateProfileFeatureActivity::class.java).apply {
                putExtra("FEATURE", "sertifikat")
            }
            activity?.startActivity(intent)
        }

        binding.cvOrganisasi.setOnClickListener {
            val intent = Intent(activity, CreateProfileFeatureActivity::class.java).apply {
                putExtra("FEATURE", "organisasi")
            }
            activity?.startActivity(intent)
        }

        binding.btnBuatCurriculumVite.setOnClickListener {
            generateCV()
        }

        binding.btnBuatCoverLetter.setOnClickListener {
            showGenerateCoverLetterDialog()
        }

        binding.actionBarCustom.ivAvatar.setOnClickListener {
            selectImageFromGallery()
        }
    }

    private fun refreshData() {
        binding.rvPengalamanKerja.visibility = View.GONE
        binding.rvPendidikan.visibility = View.GONE
        binding.rvSertifikat.visibility = View.GONE
        binding.rvOrganisasi.visibility = View.GONE

        binding.loadingPengalamanKerja.visibility = View.VISIBLE
        binding.loadingPendidikan.visibility = View.VISIBLE
        binding.loadingSertifikat.visibility = View.VISIBLE
        binding.loadingOrganisasi.visibility = View.VISIBLE

        binding.emptyPengalamanKerja.visibility = View.GONE
        binding.emptyPendidikan.visibility = View.GONE
        binding.emptySertifikat.visibility = View.GONE
        binding.emptyOrganisasi.visibility = View.GONE


        db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val summary = it.getString("profile_summary")

                if(summary != null){
                    binding.descriptionTentangSaya.text = summary
                    profileSummary = summary
                } else {
                    binding.descriptionTentangSaya.text = "Tidak ada data"
                    profileSummary = "Tidak ada data"
                }

                binding.actionBarCustom.tvNamaLengkap.text = it.getString("nama")
                binding.actionBarCustom.tvJurusan.text = it.getString("jurusan")

                Glide.with(requireContext())
                    .load(it.getString("author_img_url"))
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(binding.actionBarCustom.ivAvatar)
            }

        db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid)
            .collection("pengalaman_kerja").get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    binding.emptyPengalamanKerja.visibility = View.VISIBLE
                } else {
                    pengalamanKerjaList.clear()

                    pengalamanKerjaList.addAll(
                        documents.map { it.toObject(ProfileFeature::class.java) }
                    )

                    pengalamanKerjaAdapter.setData(pengalamanKerjaList)

                    binding.rvPengalamanKerja.visibility = View.VISIBLE
                }

                binding.loadingPengalamanKerja.visibility = View.GONE
            }

        db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid)
            .collection("pendidikan").get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    binding.emptyPendidikan.visibility = View.VISIBLE
                } else {
                    pendidikanList.clear()

                    pendidikanList.addAll(
                        documents.map { it.toObject(ProfileFeature::class.java) }
                    )

                    pendidikanAdapter.setData(pendidikanList)

                    binding.rvPendidikan.visibility = View.VISIBLE

                }

                binding.loadingPendidikan.visibility = View.GONE
            }

        db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid)
            .collection("sertifikat").get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    binding.emptySertifikat.visibility = View.VISIBLE
                } else {
                    sertifikatList.clear()

                    sertifikatList.addAll(
                        documents.map { it.toObject(ProfileFeature::class.java) }
                    )

                    sertifikatAdapter.setData(sertifikatList)

                    binding.rvSertifikat.visibility = View.VISIBLE

                }

                binding.loadingSertifikat.visibility = View.GONE
            }

        db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid)
            .collection("organisasi").get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    binding.emptyOrganisasi.visibility = View.VISIBLE
                } else {
                    organisasiList.clear()

                    organisasiList.addAll(
                        documents.map { it.toObject(ProfileFeature::class.java) }
                    )

                    organisasiAdapter.setData(organisasiList)

                    binding.rvOrganisasi.visibility = View.VISIBLE

                }

                binding.loadingOrganisasi.visibility = View.GONE
            }
    }

    private fun showSummaryDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.input_profile_summary_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val positiveButton = dialog.findViewById(R.id.btn_save) as TextView
        val negativeButton = dialog.findViewById(R.id.btn_cancel) as TextView

        val edtSummary = dialog.findViewById(R.id.edt_summary) as EditText

        edtSummary.setText(binding.descriptionTentangSaya.text.toString())

        positiveButton.setOnClickListener {
            val summary = edtSummary.text.toString()
            val uid = firebaseAuth.currentUser?.uid.toString()

            db.collection("users_mobile").document(uid)
                .update("profile_summary", summary)
                .addOnSuccessListener {
                    refreshData()
                    dialog.dismiss()
                }
        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        val window = dialog.window
        window!!.attributes = lp
    }

    private fun showGenerateCoverLetterDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.input_cover_letter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val positiveButton = dialog.findViewById(R.id.btn_create) as TextView
        val negativeButton = dialog.findViewById(R.id.btn_cancel) as TextView

        val edtCompany = dialog.findViewById(R.id.edt_company) as EditText
        val edtPosition = dialog.findViewById(R.id.edt_position) as EditText

        positiveButton.setOnClickListener {
            val company = edtCompany.text.toString()
            val position = edtPosition.text.toString()

            if (company.isNotEmpty() && position.isNotEmpty()){

                db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        val intent = Intent(activity, CoverLetterActivity::class.java).apply {
                            putExtra("COMPANY", company)
                            putExtra("POSITION", position)
                            putExtra("NAME", it.getString("nama"))
                        }
                        activity?.startActivity(intent)
                    }
            } else {
                BeautifulDialog.build(requireActivity())
                    .title("Gagal", titleColor = ContextCompat.getColor(requireContext(), R.color.black), fontStyle = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold))
                    .description("Harap isi semua kolom",  color = ContextCompat.getColor(requireContext(), R.color.black), fontStyle = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium))
                    .type(type= BeautifulDialog.TYPE.ERROR)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .hideNegativeButton(true)
                    .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_btn_blue, textColor = ContextCompat.getColor(requireContext(), R.color.white), fontStyle = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold), shouldIDismissOnClick = true) {
                    }
            }

        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        val window = dialog.window
        window!!.attributes = lp
    }

    private fun generateCV() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            binding.btnBuatCurriculumVite.text = "..."
            binding.btnBuatCurriculumVite.isClickable = false

            db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener { userData ->
                    val fileName = "cv.pdf"
                    val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + fileName

                    // Create a new document
                    val document = Document()

                    // Create a PdfWriter instance
                    PdfWriter.getInstance(document, FileOutputStream(filePath))

                    // Open the document
                    document.open()

                    // Add person information
                    val nameFont = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD)
                    val contactFont = Font(Font.FontFamily.TIMES_ROMAN, 14f)
                    val headerFont = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
                    val featureTitleFont = Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.BOLD)
                    val featureSubTitleFont = Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.ITALIC)
                    val featureDateFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.ITALIC)

                    val spacing = Paragraph("")
                    spacing.spacingAfter = 12f

                    val spacingBig = Paragraph("")
                    spacingBig.spacingAfter = 24f

                    val featureSeparator = LineSeparator()

                    val personTable = PdfPTable(1)
                    personTable.widthPercentage = 100f
                    personTable.addCell(PdfPCell(Phrase(userData.getString("nama"), nameFont)).apply { border = Rectangle.NO_BORDER; horizontalAlignment = Element.ALIGN_CENTER })
                    personTable.addCell(PdfPCell(Phrase(userData.getString("email"), contactFont)).apply { border = Rectangle.NO_BORDER; horizontalAlignment = Element.ALIGN_CENTER })
                    document.add(personTable)

                    val summary = Paragraph(profileSummary)
                    summary.spacingBefore = 24f

                    document.add(summary)

                    val experienceHeader = Paragraph("PENGALAMAN KERJA", headerFont)
                    experienceHeader.spacingBefore = 24f
                    experienceHeader.spacingAfter = 8f

                    document.add(experienceHeader)
                    document.add(featureSeparator)
                    document.add(spacing)

                    for (experience in pengalamanKerjaList) {
                        val experienceTable = PdfPTable(2)
                        experienceTable.widthPercentage = 100f
                        experienceTable.addCell(PdfPCell(Phrase(experience.title, featureTitleFont)).apply { border = Rectangle.NO_BORDER })
                        experienceTable.addCell(PdfPCell(Phrase(experience.date, featureDateFont)).apply { border = Rectangle.NO_BORDER; horizontalAlignment = Element.ALIGN_RIGHT })
                        experienceTable.addCell(PdfPCell(Phrase(experience.sub_title, featureSubTitleFont)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        experienceTable.addCell(PdfPCell(Phrase(experience.description)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        experienceTable.spacingAfter = 12f
                        document.add(experienceTable)
                    }


                    val educationHeader = Paragraph("RIWAYAT PENDIDIKAN", headerFont)
                    educationHeader.spacingBefore = 24f
                    educationHeader.spacingAfter = 8f

                    document.add(educationHeader)
                    document.add(featureSeparator)
                    document.add(spacing)

                    for (education in pendidikanList) {
                        val educationTable = PdfPTable(2)
                        educationTable.widthPercentage = 100f
                        educationTable.addCell(PdfPCell(Phrase(education.title, featureTitleFont)).apply { border = Rectangle.NO_BORDER })
                        educationTable.addCell(PdfPCell(Phrase(education.date, featureDateFont)).apply { border = Rectangle.NO_BORDER; horizontalAlignment = Element.ALIGN_RIGHT })
                        educationTable.addCell(PdfPCell(Phrase(education.sub_title, featureSubTitleFont)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        educationTable.addCell(PdfPCell(Phrase(education.description)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        educationTable.spacingAfter = 12f
                        document.add(educationTable)
                    }

                    val certificateHeader = Paragraph("SERTIFIKAT", headerFont)
                    certificateHeader.spacingBefore = 24f
                    certificateHeader.spacingAfter = 8f

                    document.add(certificateHeader)
                    document.add(featureSeparator)
                    document.add(spacing)

                    for (certificate in sertifikatList) {
                        val certificateTable = PdfPTable(2)
                        certificateTable.widthPercentage = 100f
                        certificateTable.addCell(PdfPCell(Phrase(certificate.title, featureTitleFont)).apply { border = Rectangle.NO_BORDER })
                        certificateTable.addCell(PdfPCell(Phrase(certificate.date, featureDateFont)).apply { border = Rectangle.NO_BORDER; horizontalAlignment = Element.ALIGN_RIGHT })
                        certificateTable.addCell(PdfPCell(Phrase(certificate.sub_title, featureSubTitleFont)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        certificateTable.addCell(PdfPCell(Phrase(certificate.description)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        certificateTable.spacingAfter = 12f
                        document.add(certificateTable)
                    }

                    val orgnizationHeader = Paragraph("PENGALAMAN ORGANISASI", headerFont)
                    orgnizationHeader.spacingBefore = 24f
                    orgnizationHeader.spacingAfter = 8f

                    document.add(orgnizationHeader)
                    document.add(featureSeparator)
                    document.add(spacing)

                    for (orgnization in organisasiList) {
                        val orgnizationTable = PdfPTable(2)
                        orgnizationTable.widthPercentage = 100f
                        orgnizationTable.addCell(PdfPCell(Phrase(orgnization.title, featureTitleFont)).apply { border = Rectangle.NO_BORDER })
                        orgnizationTable.addCell(PdfPCell(Phrase(orgnization.date, featureDateFont)).apply { border = Rectangle.NO_BORDER; horizontalAlignment = Element.ALIGN_RIGHT })
                        orgnizationTable.addCell(PdfPCell(Phrase(orgnization.sub_title, featureSubTitleFont)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        orgnizationTable.addCell(PdfPCell(Phrase(orgnization.description)).apply { border = Rectangle.NO_BORDER; colspan = 2 })
                        orgnizationTable.spacingAfter = 12f
                        document.add(orgnizationTable)
                    }

                    document.close()

                    BeautifulDialog.build(requireActivity())
                        .title("Berhasil", titleColor = ContextCompat.getColor(requireContext(), R.color.black), fontStyle = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold))
                        .description("CV Berhasil dibuat",  color = ContextCompat.getColor(requireContext(), R.color.black), fontStyle = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium))
                        .type(type= BeautifulDialog.TYPE.SUCCESS)
                        .position(BeautifulDialog.POSITIONS.CENTER)
                        .onPositive(text = "Buka CV", buttonBackgroundColor = R.drawable.bg_btn_blue, textColor = ContextCompat.getColor(requireContext(), R.color.white), fontStyle = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold), shouldIDismissOnClick = true) {
                            val file = File(filePath)
                            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(uri, "application/pdf")
                            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(intent)
                        }
                        .onNegative(text = "Tutup", buttonBackgroundColor = R.drawable.bg_outline_blue_rounded, textColor = ContextCompat.getColor(requireContext(), R.color.blue100), fontStyle = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold), shouldIDismissOnClick = true) {
                        }

                    binding.btnBuatCurriculumVite.text = "CV"
                    binding.btnBuatCurriculumVite.isClickable = true
                }
        }
    }


    private fun showSettingPopup(v: View) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.profile_setting_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.lamaran -> {
                    val intent = Intent(activity, LamaranStatusActivity::class.java)
                    activity?.startActivity(intent)
                }
                R.id.sertifikat -> {
                    val intent = Intent(activity, CertificateRepositoryActivity::class.java)
                    activity?.startActivity(intent)
                }
                R.id.logout -> {
                    firebaseAuth.signOut()
                    findNavController().navigate(R.id.action_navigation_profile_to_authActivity)
                    requireActivity().finish()
                }
            }
            true
        }

        popup.show()
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Please select..."),
            GALLERY_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            val file_uri = data.data
            if (file_uri != null) {
                uploadImageToFirebase(file_uri)
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
        refStorage.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    val imageUrl = it.toString()

                    db.collection("users_mobile").document(firebaseAuth.currentUser!!.uid)
                        .update("author_img_url", imageUrl)

                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.avatar_placeholder)
                        .into(binding.actionBarCustom.ivAvatar)
                }
            }
            .addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1
    }
}