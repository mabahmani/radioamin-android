package ir.mab.radioamin.ui.deviceonly.devicefilesoption

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.BottomsheetDeivceFilesOptionBinding
import ir.mab.radioamin.databinding.DialogAddToPlaylistBinding
import ir.mab.radioamin.databinding.DialogCreatePlaylistBinding
import ir.mab.radioamin.vm.DevicePlaylistsViewModel
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber

@AndroidEntryPoint
class DeviceFilesOptionBottomSheet(
    private val id: Long,
    private val title: String,
    private val subtitle: String,
    private val thumbnail: Bitmap?,
    private val type: DeviceFileType
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetDeivceFilesOptionBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()

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

        binding.addToPlaylistParent.setOnClickListener {
            addToPlaylist(id, type)
        }

    }

    private fun addToPlaylist(id: Long, type: DeviceFileType) {

        val addToPlaylistDialogAdapter = AddToPlaylistDialogAdapter(mutableListOf())
        val dialogAddToPlaylistBinding =
            DialogAddToPlaylistBinding.inflate(LayoutInflater.from(requireContext()))

        dialogAddToPlaylistBinding.list.adapter = addToPlaylistDialogAdapter
        dialogAddToPlaylistBinding.list.layoutManager = LinearLayoutManager(requireContext())

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogAddToPlaylistBinding.root)
            .show()

        dialogAddToPlaylistBinding.createNewPlaylistButton.setOnClickListener {
            showCreateNewPlaylistDialog()
            dialog.dismiss()
        }

        dialogAddToPlaylistBinding.closeIcon.setOnClickListener {
            dialog.dismiss()
        }


        devicePlaylistsViewModel.getDevicePlaylists().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    dialogAddToPlaylistBinding.showProgress = true
                }

                Status.SUCCESS -> {
                    Timber.d("devicePlaylistsViewModel %s", it)
                    addToPlaylistDialogAdapter.list = it.data ?: mutableListOf()
                    addToPlaylistDialogAdapter.notifyDataSetChanged()

                    dialogAddToPlaylistBinding.showProgress = false
                }

                Status.ERROR -> {
                    dialogAddToPlaylistBinding.showProgress = false
                }
            }
        })

    }

    private fun showCreateNewPlaylistDialog() {

        val dialogCreatePlaylistBinding = DialogCreatePlaylistBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogCreatePlaylistBinding.root)
            .show()


        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)


        dialogCreatePlaylistBinding.title.addTextChangedListener {
            if (TextUtils.isEmpty(it)){
                dialogCreatePlaylistBinding.positiveButton.isEnabled = false
                dialogCreatePlaylistBinding.positiveButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.color5))
            }
            else{
                dialogCreatePlaylistBinding.positiveButton.isEnabled = true
                dialogCreatePlaylistBinding.positiveButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
        }

        dialogCreatePlaylistBinding.positiveButton.setOnClickListener{
            createNewPlaylist(dialogCreatePlaylistBinding.title.text.toString(), dialog)
        }

        dialogCreatePlaylistBinding.negativeButton.setOnClickListener{
            dialog.dismiss()
        }


    }

    private fun createNewPlaylist(title: String, dialog: AlertDialog) {
        devicePlaylistsViewModel.createPlaylist(title).observe(viewLifecycleOwner,{
            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    dialog.dismiss()
                }

                Status.ERROR ->{
                    Timber.e(it.message)
                }
            }
        })
    }
}