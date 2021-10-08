package ir.mab.radioamin.ui.main.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemHomeTopicMusicBinding
import ir.mab.radioamin.vo.Music

class ExploreMusicAdapter(private val list: List<Music>): RecyclerView.Adapter<ExploreMusicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHomeTopicMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title = list[position].name
        holder.binding.subtitle = list[position].singer.name
        holder.binding.coverUrl = list[position].cover.url
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView:ItemHomeTopicMusicBinding): RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
        init {
            itemView.executePendingBindings()
        }
    }
}