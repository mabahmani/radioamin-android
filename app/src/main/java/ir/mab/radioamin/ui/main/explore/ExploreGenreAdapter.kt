package ir.mab.radioamin.ui.main.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemExploreGenreBinding
import ir.mab.radioamin.vo.Genre

class ExploreGenreAdapter(private val list: List<Genre>): RecyclerView.Adapter<ExploreGenreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExploreGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.name = list[position].name
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView:ItemExploreGenreBinding): RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
        init {
            itemView.executePendingBindings()
        }
    }
}