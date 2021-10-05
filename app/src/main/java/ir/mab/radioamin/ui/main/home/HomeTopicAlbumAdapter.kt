package ir.mab.radioamin.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemHomeTopicAlbumBinding
import ir.mab.radioamin.vo.Album

class HomeTopicAlbumAdapter(private val list: List<Album>): RecyclerView.Adapter<HomeTopicAlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHomeTopicAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title = list[position].name
        holder.binding.subtitle = list[position].singer.name
        holder.binding.coverUrl = list[position].cover.url
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView:ItemHomeTopicAlbumBinding): RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
        init {
            itemView.executePendingBindings()
        }
    }
}