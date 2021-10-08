package ir.mab.radioamin.ui.deviceonly.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemPlayerQueueBinding
import ir.mab.radioamin.ui.deviceonly.listener.PlayerQueueItemListeners
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.util.DeviceFilesImageLoader.getDeviceAlbumThumbnail
import ir.mab.radioamin.vo.devicefiles.DeviceSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlayerQueueSongsAdapter(var list: List<DeviceSong>, var playerQueueItemListeners: PlayerQueueItemListeners) :
    RecyclerView.Adapter<PlayerQueueSongsAdapter.ViewHolder>() {

    var currentMediaPosition = -1
    var preMediaPosition = -1
    var isPlaying = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPlayerQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false), playerQueueItemListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

        if (currentMediaPosition == position){
            holder.binding.equalizer.visibility = View.VISIBLE

            if (isPlaying){
                holder.binding.equalizer.playAnimation()
            }
            else{
                holder.binding.equalizer.pauseAnimation()
            }
        }

        else if (preMediaPosition == position){
            holder.binding.equalizer.visibility = View.GONE
        }
        else{
            holder.binding.equalizer.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView:ItemPlayerQueueBinding,
        var editDevicePlaylistItemListeners: PlayerQueueItemListeners
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

            binding.handler.setOnTouchListener { _, event ->
                if (event.actionMasked ==
                    MotionEvent.ACTION_DOWN) {
                    editDevicePlaylistItemListeners.onStartDrag(this)
                }
                false
            }

            binding.parent.setOnClickListener {
                editDevicePlaylistItemListeners.onClick(bindingAdapterPosition)
            }
        }
    }
}