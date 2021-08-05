package ir.mab.radioamin.ui.deviceonly.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ItemArtistBinding
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.getDeviceThumbnailAlbumArt
import ir.mab.radioamin.vo.DeviceArtist
import ir.mab.radioamin.vo.DeviceFileType

class DeviceArtistsAdapter(var list: List<DeviceArtist>, var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners) :
    RecyclerView.Adapter<DeviceArtistsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtistBinding.inflate(LayoutInflater.from(parent.context)), deviceFilesMoreOnClickListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView: ItemArtistBinding,
        var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemArtistBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceArtist) {
            binding.thumbnail = itemView.context.getDeviceThumbnailAlbumArt(model.albumId?: -1)
            binding.artist = model

            val bundle = bundleOf(AppConstants.Arguments.ARTIST_ID to model.id, AppConstants.Arguments.ARTIST_NAME to model.name)
            binding.parent.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_deviceArtistsFragment_to_deviceArtistFragment, bundle))

            binding.more.setOnClickListener {
                deviceFilesMoreOnClickListeners.onShowOptions(
                    model.id?: -1,
                    binding.title.text.toString(),
                    binding.subtitle.text.toString(),
                    binding.thumbnail,
                    DeviceFileType.ARTIST
                )
            }

            binding.parent.setOnLongClickListener {
                //it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                binding.more.performClick()
                true
            }
        }
    }
}