package com.example.androidappassignment

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NewsListAdapter(private val listener: NewsItemClicked) :
    RecyclerView.Adapter<NewsViewHolder>() {

    private var items: ArrayList<News> = ArrayList()

    fun setFilteredList(items: ArrayList<News>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        val viewHolder = NewsViewHolder(view)
        view.setOnClickListener {
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        holder.title.text = currentItem.title
        holder.description.text = currentItem.description
        holder.publishedAt.text = calculatePublishedAt(currentItem.publishedAt)
        holder.source.text = currentItem.source
        Glide.with(holder.itemView.context).load(currentItem.image).into(holder.image)
    }

    private fun calculatePublishedAt(publishedAt: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        try {
            val time: Long = sdf.parse(publishedAt).time
            val now = System.currentTimeMillis()
            val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
            return ago.toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateNews(updatedNews: ArrayList<News>) {
        items.clear()
        items.addAll(updatedNews)

        notifyDataSetChanged()
    }
}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.title)
    val description: TextView = itemView.findViewById(R.id.description)
    val publishedAt: TextView = itemView.findViewById(R.id.publishedAt)
    val source: TextView = itemView.findViewById(R.id.source)
    val image: ImageView = itemView.findViewById(R.id.image)
}

interface NewsItemClicked {
    fun onItemClicked(item: News)
}