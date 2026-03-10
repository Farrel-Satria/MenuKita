package com.example.menukita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.menukita.R
import com.example.menukita.model.BannerItem
import com.example.menukita.util.TextEffects
import java.text.NumberFormat
import java.util.Locale

class BannerAdapter(
    private val items: List<BannerItem>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBadge: TextView = view.findViewById(R.id.tvBadge)
        val tvTag: TextView = view.findViewById(R.id.tvBannerTag)
        val tvTitle: TextView = view.findViewById(R.id.tvBannerTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvBannerSubtitle)
        val tvPriceNow: TextView = view.findViewById(R.id.tvBannerPriceNow)
        val tvPriceOld: TextView = view.findViewById(R.id.tvBannerPriceOld)
        val tvHighlights: TextView = view.findViewById(R.id.tvBannerHighlights)
        val tvQuote: TextView = view.findViewById(R.id.tvBannerQuote)
        val ivBanner: ImageView = view.findViewById(R.id.ivBanner)
        val viewShimmer: View = view.findViewById(R.id.viewShimmer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        holder.tvBadge.text = item.tag
        holder.tvTag.text = item.tag
        holder.tvTitle.text = item.title
        holder.ivBanner.setImageResource(item.imageRes)

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val normalPrice = 42000 + (position * 6000)
        val promoPrice = (normalPrice * 0.78f).toInt()
        holder.tvPriceNow.text = formatter.format(promoPrice)
        TextEffects.strikethrough(holder.tvPriceOld, formatter.format(normalPrice))

        holder.tvQuote.text = TextEffects.quoteText("Promo ini paling sering dipilih saat jam makan siang.")
        holder.tvHighlights.text = TextEffects.highlightedText(
            "Diskon Gratis Promo",
            keywords = listOf("Diskon", "Gratis", "Promo"),
            bgColor = ContextCompat.getColor(context, R.color.highlight_chip_bg),
            textColor = ContextCompat.getColor(context, R.color.highlight_chip_text)
        )

        TextEffects.reveal(holder.tvTitle, delay = 20L)
        TextEffects.typewriter(holder.tvSubtitle, item.subtitle, duration = 640L) {
            TextEffects.crossfadeText(
                holder.tvSubtitle,
                TextEffects.highlightedText(
                    item.subtitle,
                    keywords = listOf("Diskon", "Gratis", "Promo", "Baru"),
                    bgColor = ContextCompat.getColor(context, R.color.highlight_chip_bg),
                    textColor = ContextCompat.getColor(context, R.color.highlight_chip_text)
                )
            )
        }
        holder.viewShimmer.animate().cancel()
        holder.itemView.post {
            val stripWidth = holder.viewShimmer.width.toFloat().coerceAtLeast(120f)
            val endX = holder.itemView.width.toFloat() + stripWidth
            holder.viewShimmer.translationX = -stripWidth
            holder.viewShimmer.animate()
                .translationX(endX)
                .setDuration(1100)
                .start()
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: BannerViewHolder) {
        super.onViewRecycled(holder)
        TextEffects.clear(holder.tvTitle)
        TextEffects.clear(holder.tvSubtitle)
        holder.viewShimmer.animate().cancel()
    }
}
