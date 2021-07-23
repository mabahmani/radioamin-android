package ir.mab.radioamin.ui.deviceonly.album

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemAlbumSongBinding
import ir.mab.radioamin.ui.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.DeviceSong

class DeviceAlbumSongsAdapter(var list: List<DeviceSong>, var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners) :
    RecyclerView.Adapter<DeviceAlbumSongsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumSongBinding.inflate(LayoutInflater.from(parent.context)), deviceFilesMoreOnClickListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView: ItemAlbumSongBinding,
        var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemAlbumSongBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceSong) {
            binding.song = model
            binding.duration = DateTimeFormatter.durationToHumanTime(model.duration?:0)

            binding.more.setOnClickListener {
                deviceFilesMoreOnClickListeners.onShowOptions(
                    model.id?: -1,
                    binding.title.text.toString(),
                    binding.subtitle.text.toString(),
                    model.thumbnail,
                    DeviceFileType.SONG
                )
            }

            binding.parent.setOnLongClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                binding.more.performClick()
                true
            }
        }
    }
}