package ir.mab.radioamin.ui.deviceonly.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemAlbumSongBinding
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.vo.DeviceSong

class DeviceAlbumSongsAdapter(var list: List<DeviceSong>) :
    RecyclerView.Adapter<DeviceAlbumSongsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumSongBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: ItemAlbumSongBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemAlbumSongBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceSong) {
            binding.song = model
            binding.duration = DateTimeFormatter.durationToHumanTime(model.duration?:0)
        }
    }
}