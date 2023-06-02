package com.example.animenotifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AnimeAdapter(
    private var animeList: List<Anime>,
    private val onItemClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    inner class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        // set your views here
        // for example:
        holder.titleTextView.text = anime.title
        // for the image, you might use an image loading library like Glide or Picasso
        // Glide.with(holder.imageView).load(anime.imageUrl).into(holder.imageView)
        holder.itemView.setOnClickListener { onItemClick(anime) }
    }

    override fun getItemCount(): Int {
        return animeList.size
    }

    fun updateList(newList: List<Anime>) {
        animeList = newList
        notifyDataSetChanged()
    }
}
