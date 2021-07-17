package ir.mab.radioamin.ui.deviceonly.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ItemAlbumBinding
import ir.mab.radioamin.util.AppConstants
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

            val bundle = bundleOf(AppConstants.Arguments.ALBUM_ID to model.id, AppConstants.Arguments.ALBUM_NAME to model.name)
            binding.parent.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_deviceAlbumsFragment_to_deviceAlbumFragment, bundle))

        }
    }
}