package ir.mab.radioamin.ui.deviceonly

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.mab.radioamin.databinding.BottomsheetDeivceFilesOptionBinding
import ir.mab.radioamin.vo.DeviceFileType

class DeviceFilesOptionBottomSheet(
    private val id: Long,
    private val title: String,
    private val subtitle: String,
    private val thumbnail: Bitmap?,
    private val type: DeviceFileType
) : BottomSheetDialogFragment() {
    lateinit var binding: BottomsheetDeivceFilesOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetDeivceFilesOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title = this.title
        binding.subtitle = this.subtitle
        binding.thumbnail = this.thumbnail
        binding.type = this.type

    }
}