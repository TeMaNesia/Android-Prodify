package com.inovego.temanesia.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.ActivityCoverLetterBinding
import com.inovego.temanesia.databinding.ActivityCreateProfileFeatureBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class CoverLetterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoverLetterBinding

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoverLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val company = intent.getStringExtra("COMPANY")
        val position = intent.getStringExtra("POSITION")
        val name = intent.getStringExtra("NAME")

        val apiKey="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"
        val url="https://api.openai.com/v1/chat/completions"

        val requestBody="""
            {
                "model": "gpt-3.5-turbo",
                    "messages": [
                        {
                            "role": "user",
                            "content": "Tolong buatkan saya cover letter dalam bahasa Indonesia untuk perusahaan $company posisi $position, nama lengkap saya adalah ${name}"
                        }
                    ]
             }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)

                    val jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val textResult = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")

                    Log.v("data",textResult)

                    runOnUiThread {
                        binding.edtContent.setText(textResult)
                        binding.loading.visibility = View.GONE
                        binding.edtContent.visibility = View.VISIBLE
                        binding.btnCopy.visibility = View.VISIBLE
                    }
                }
                else{
                    Log.v("data","empty")
                }
            }

        })

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("letter", binding.edtContent.text.toString())
            clipboard.setPrimaryClip(clip)

            BeautifulDialog.build(this)
                .title("Berhasil", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Cover Letter berhasil disalin ke clipboard!",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.SUCCESS)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .hideNegativeButton(true)
                .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_btn_blue, textColor = ContextCompat.getColor(this, R.color.white), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold), shouldIDismissOnClick = true) {
                }
        }
    }
}