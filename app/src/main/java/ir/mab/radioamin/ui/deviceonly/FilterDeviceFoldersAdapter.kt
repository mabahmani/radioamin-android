package ir.mab.radioamin.ui.deviceonly

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.databinding.ItemFilterFolderBinding
import ir.mab.radioamin.vo.devicefiles.DeviceSongFolder

class FilterDeviceFoldersAdapter(var list: List<DeviceSongFolder>):
    RecyclerView.Adapter<FilterDeviceFoldersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFilterFolderBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView:ItemFilterFolderBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemFilterFolderBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(model: DeviceSongFolder) {
            binding.check = model.selected
            binding.path = model.path
            binding.parent.setOnClickListener {
                binding.checkBox.performClick()
            }

            binding.checkBox.setOnCheckedChangeListener { _, b ->
                model.selected = b
                binding.check = b
            }
        }
    }
}