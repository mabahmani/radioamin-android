package ir.mab.radioamin.ui.deviceonly.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemPlayerQueueBinding
import ir.mab.radioamin.ui.deviceonly.listener.PlayerQueueItemDragListeners
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.util.DeviceFilesImageLoader.getDeviceAlbumThumbnail
import ir.mab.radioamin.vo.DeviceSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerQueueSongsAdapter(var list: List<DeviceSong>, var playerQueueItemDragListeners: PlayerQueueItemDragListeners) :
    RecyclerView.Adapter<PlayerQueueSongsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPlayerQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false), playerQueueItemDragListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView:ItemPlayerQueueBinding,
        var editDevicePlaylistItemDragListeners: PlayerQueueItemDragListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemPlayerQueueBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(model: DeviceSong) {
            GlobalScope.launch(Dispatchers.IO){
                binding.thumbnail = itemView.context.getDeviceAlbumThumbnail(model.albumId?: -1)
            }
            binding.song = model
            binding.duration = DateTimeFormatter.millisToHumanTime(model.duration?:0)

            Timber.d("bind %s %s", model, bindingAdapterPosition)
            binding.handler.setOnTouchListener { _, event ->
                if (event.actionMasked ==
                    MotionEvent.ACTION_DOWN) {
                    editDevicePlaylistItemDragListeners.onStartDrag(this)
                }
                false
            }
        }
    }
}