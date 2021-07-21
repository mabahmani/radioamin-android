package ir.mab.radioamin.ui.deviceonly.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ItemAlbumBinding
import ir.mab.radioamin.ui.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.DeviceAlbum
import ir.mab.radioamin.vo.DeviceFileType

class DeviceAlbumsAdapter(var list: List<DeviceAlbum>, var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners) :
    RecyclerView.Adapter<DeviceAlbumsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumBinding.inflate(LayoutInflater.from(parent.context)), deviceFilesMoreOnClickListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView: ItemAlbumBinding,
        var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemAlbumBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceAlbum) {
            binding.album = model

            val bundle = bundleOf(AppConstants.Arguments.ALBUM_ID to model.id, AppConstants.Arguments.ALBUM_NAME to model.name)
            binding.parent.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_deviceAlbumsFragment_to_deviceAlbumFragment, bundle))

            binding.more.setOnClickListener {
                deviceFilesMoreOnClickListeners.onShowOptions(
                    model.id?: -1,
                    binding.title.text.toString(),
                    binding.subtitle.text.toString(),
                    model.thumbnail,
                    DeviceFileType.ALBUM
                )
            }
        }
    }
}