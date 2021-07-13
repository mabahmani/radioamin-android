package ir.mab.radioamin.ui.deviceonly.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemAlbumBinding
import ir.mab.radioamin.vo.DeviceAlbum

class DeviceAlbumsAdapter(var list: List<DeviceAlbum>) :
    RecyclerView.Adapter<DeviceAlbumsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: ItemAlbumBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemAlbumBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceAlbum) {
            binding.album = model
        }
    }
}