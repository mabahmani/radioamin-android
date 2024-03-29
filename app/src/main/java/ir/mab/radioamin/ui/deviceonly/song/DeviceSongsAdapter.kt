package ir.mab.radioamin.ui.deviceonly.song

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemSongBinding
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceSongsOnClickListeners
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.util.DeviceFilesImageLoader.getDeviceAlbumThumbnail
import ir.mab.radioamin.vo.devicefiles.DeviceFileType
import ir.mab.radioamin.vo.devicefiles.DeviceSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DeviceSongsAdapter(var list: List<DeviceSong>,
                         var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners,
                         var deviceSongsOnClickListeners: DeviceSongsOnClickListeners
                         ) :
    RecyclerView.Adapter<DeviceSongsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false), deviceFilesMoreOnClickListeners, deviceSongsOnClickListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView: ItemSongBinding,
        var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners,
        var deviceSongsOnClickListeners: DeviceSongsOnClickListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSongBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceSong) {
            GlobalScope.launch(Dispatchers.IO){
                binding.thumbnail = itemView.context.getDeviceAlbumThumbnail(model.albumId ?: -1)
            }
            binding.song = model
            binding.duration = DateTimeFormatter.millisToHumanTime(model.duration?:0)

            binding.more.setOnClickListener {
                deviceFilesMoreOnClickListeners.onShowOptions(
                    model.id?: -1,
                    binding.title.text.toString(),
                    binding.subtitle.text.toString(),
                    binding.thumbnail,
                    DeviceFileType.SONG
                )
            }

            binding.parent.setOnLongClickListener {
                //it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                binding.more.performClick()
                true
            }

            binding.parent.setOnClickListener {
                deviceSongsOnClickListeners.onSongClick(bindingAdapterPosition)
            }
        }
    }
}