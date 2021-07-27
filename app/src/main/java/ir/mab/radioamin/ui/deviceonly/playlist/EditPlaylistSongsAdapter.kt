package ir.mab.radioamin.ui.deviceonly.playlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemSongEditPlaylistBinding
import ir.mab.radioamin.ui.listener.EditDevicePlaylistItemDragListeners
import ir.mab.radioamin.util.DateTimeFormatter
import ir.mab.radioamin.vo.DeviceSong

class EditPlaylistSongsAdapter(var list: List<DeviceSong>, var editDevicePlaylistItemDragListeners: EditDevicePlaylistItemDragListeners) :
    RecyclerView.Adapter<EditPlaylistSongsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSongEditPlaylistBinding.inflate(LayoutInflater.from(parent.context)), editDevicePlaylistItemDragListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView: ItemSongEditPlaylistBinding,
        var editDevicePlaylistItemDragListeners: EditDevicePlaylistItemDragListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSongEditPlaylistBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(model: DeviceSong) {
            binding.song = model
            binding.duration = DateTimeFormatter.durationToHumanTime(model.duration?:0)

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