package ir.mab.radioamin.ui.deviceonly.genre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ItemGenreBinding
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vo.devicefiles.DeviceFileType
import ir.mab.radioamin.vo.devicefiles.DeviceGenre

class DeviceGenresAdapter(
    var list: List<DeviceGenre>,
    var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners
) :
    RecyclerView.Adapter<DeviceGenresAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGenreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), deviceFilesMoreOnClickListeners
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(
        itemView: ItemGenreBinding,
        var deviceFilesMoreOnClickListeners: DeviceFilesMoreOnClickListeners
    ) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemGenreBinding = itemView

        init {
            itemView.executePendingBindings()
        }

        fun bind(model: DeviceGenre) {
            binding.genre = model

            val bundle = bundleOf(
                AppConstants.Arguments.GENRE_ID to model.id,
                AppConstants.Arguments.GENRE_NAME to model.name
            )
            binding.parent.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_deviceGenresFragment_to_deviceGenreFragment,
                    bundle
                )
            )

            binding.more.setOnClickListener {
                deviceFilesMoreOnClickListeners.onShowOptions(
                    model.id ?: -1,
                    binding.title.text.toString(),
                    binding.subtitle.text.toString(),
                    null,
                    DeviceFileType.GENRE
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