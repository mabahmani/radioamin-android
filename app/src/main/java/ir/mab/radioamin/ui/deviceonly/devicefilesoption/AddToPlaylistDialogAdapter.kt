package ir.mab.radioamin.ui.deviceonly.devicefilesoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemAddToPlaylistDialogBinding
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionAddToPlaylistOnClickListener
import ir.mab.radioamin.vo.DevicePlaylist

class AddToPlaylistDialogAdapter(
    var list: List<DevicePlaylist>,
    var deviceFilesOptionAddToPlaylistOnClickListener: DeviceFilesOptionAddToPlaylistOnClickListener
) : RecyclerView.Adapter<AddToPlaylistDialogAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddToPlaylistDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), deviceFilesOptionAddToPlaylistOnClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(
        itemView: ItemAddToPlaylistDialogBinding,
        var deviceFilesOptionAddToPlaylistOnClickListener: DeviceFilesOptionAddToPlaylistOnClickListener
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DevicePlaylist) {
            binding.playlistName = model.name

            binding.parent.setOnClickListener {
                deviceFilesOptionAddToPlaylistOnClickListener.onPlaylistClicked(model)
            }
        }
    }
}