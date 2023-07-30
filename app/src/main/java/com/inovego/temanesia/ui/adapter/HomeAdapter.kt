package com.inovego.temanesia.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.ItemListFeatureLinearBinding
import com.inovego.temanesia.utils.FIREBASE_BEASISWA
import com.inovego.temanesia.utils.FIREBASE_LOMBA
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
import com.inovego.temanesia.utils.FIREBASE_SERTIFIKASI
import com.inovego.temanesia.utils.cat
import com.inovego.temanesia.utils.loadImageFromUrl
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeAdapter(
    private val context: Context,
    private val onClick: (FeatureItem) -> Unit) :
    ListAdapter<FeatureItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListFeatureLinearBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemPosition = getItem(position)
        holder.bind(itemPosition)

    }

    inner class MyViewHolder(val binding: ItemListFeatureLinearBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(FeatureItem: FeatureItem) {
            binding.apply {
                ivFeatures.loadImageFromUrl(FeatureItem.urlPosterImg)
                tvPillFeatures.text = FeatureItem.jenisKegiatan
                tvPillLembaga.text = FeatureItem.penyelenggara
                tvFeaturesTitle.text = FeatureItem.nama
                tvFeaturesDescriptionSingkat.text = FeatureItem.ringkasan
                tvDate.text = formatter.format(FeatureItem.date).toString()

                when(FeatureItem.jenisKegiatan){
                    "Lomba" ->{
                        layoutFeatures.setBackgroundResource(R.drawable.component_card_blue_border)
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_blue_bg)
                        tvPillFeatures.setTextColor(ContextCompat.getColor(context, R.color.blue100))

                    }
                    FIREBASE_SERTIFIKASI ->{
                        layoutFeatures.setBackgroundResource(R.drawable.component_card_orange_border)
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_orange_bg)
                        tvPillFeatures.setTextColor(ContextCompat.getColor(context, R.color.orange100))
                    }
                    FIREBASE_LOWONGAN->{
                        binding.layoutFeatures.setBackgroundResource(R.drawable.component_card_red_border)
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_red_bg)
                        tvPillFeatures.setTextColor(ContextCompat.getColor(context, R.color.red100))
                    }
                    FIREBASE_BEASISWA->{
                        binding.layoutFeatures.setBackgroundResource(R.drawable.component_card_green_border)
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_green_bg)
                        tvPillFeatures.setTextColor(ContextCompat.getColor(context, R.color.green100))
                    }
                }
            }
            binding.root.setOnClickListener {
                onClick(FeatureItem)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FeatureItem> =
            object : DiffUtil.ItemCallback<FeatureItem>() {
                override fun areItemsTheSame(
                    oldFeatureItem: FeatureItem,
                    newFeatureItem: FeatureItem,
                ): Boolean {
                    return oldFeatureItem.deskripsi == newFeatureItem.deskripsi
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldFeatureItem: FeatureItem,
                    newFeatureItem: FeatureItem,
                ): Boolean {
                    return oldFeatureItem == newFeatureItem
                }
            }
    }
}


