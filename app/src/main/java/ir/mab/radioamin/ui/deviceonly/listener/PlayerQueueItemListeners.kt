package ir.mab.radioamin.ui.deviceonly.listener

import androidx.recyclerview.widget.RecyclerView

interface PlayerQueueItemListeners {
    fun onClick(position: Int)
    fun onStartDrag(viewHolder : RecyclerView.ViewHolder)
}