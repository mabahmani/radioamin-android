package ir.mab.radioamin.ui.main.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemHomeTopicSingerBinding
import ir.mab.radioamin.vo.Singer

class ExploreSingerAdapter(private val list: List<Singer>): RecyclerView.Adapter<ExploreSingerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHomeTopicSingerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title = list[position].name
        holder.binding.subtitle = list[position].followCounts.toString()
        holder.binding.coverUrl = list[position].avatar.url
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: ItemHomeTopicSingerBinding): RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
        init {
            itemView.executePendingBindings()
        }
    }
}