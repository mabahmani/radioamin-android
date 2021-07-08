package ir.mab.radioamin.ui.deviceonly

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemSongBinding
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.vo.DeviceSong

class DeviceSongsAdapter(var list: List<DeviceSong>) :
    RecyclerView.Adapter<DeviceSongsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSongBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: ItemSongBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSongBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceSong) {
            binding.song = model
            binding.duration = DateTimeFormatter.durationToHumanTime(model.duration?:0)
        }
    }
}