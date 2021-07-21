package ir.mab.radioamin.ui.deviceonly.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ItemPlaylistBinding
import ir.mab.radioamin.ui.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.DevicePlaylist

class DevicePlaylistsAdapter(var list: List<DevicePlaylist>, var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners) :
    RecyclerView.Adapter<DevicePlaylistsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context)), deviceFilesMoreOnClickListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: ItemPlaylistBinding, var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemPlaylistBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DevicePlaylist) {
            binding.playlist = model

            val bundle = bundleOf(
                AppConstants.Arguments.PLAYLIST_ID to model.id,
                AppConstants.Arguments.PLAYLIST_NAME to model.name
            )
            binding.parent.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_devicePlaylistsFragment_to_devicePlaylistFragment,
                    bundle
                )
            )

            binding.more.setOnClickListener {
                deviceFilesMoreOnClickListeners.onShowOptions(
                    model.id?: -1,
                    binding.title.text.toString(),
                    binding.subtitle.text.toString(),
                    model.thumbnail,
                    DeviceFileType.PLAYLIST
                )
            }
        }
    }
}