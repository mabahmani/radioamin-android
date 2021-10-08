package ir.mab.radioamin.ui.deviceonly.album

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemAlbumSongBinding
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceSongsOnClickListeners
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.vo.devicefiles.DeviceFileType
import ir.mab.radioamin.vo.devicefiles.DeviceSong

class DeviceAlbumSongsAdapter(
    var list: List<DeviceSong>,
    var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners,
    var deviceSongsOnClickListeners: DeviceSongsOnClickListeners

) :
    RecyclerView.Adapter<DeviceAlbumSongsAdapter.ViewHolder>() {

    internal var albumArt:Bitmap? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumSongBinding.inflate(LayoutInflater.from(parent.context)),
            deviceFilesMoreOnClickListeners
            ,deviceSongsOnClickListeners
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], albumArt)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView: ItemAlbumSongBinding,
        var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners,
        var deviceSongsOnClickListeners: DeviceSongsOnClickListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemAlbumSongBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceSong, albumArt: Bitmap?) {
            binding.song = model
            binding.duration = DateTimeFormatter.millisToHumanTime(model.duration?:0)

            binding.more.setOnClickListener {
                deviceFilesMoreOnClickListeners.onShowOptions(
                    model.id?: -1,
                    binding.title.text.toString(),
                    binding.subtitle.text.toString(),
                    albumArt,
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