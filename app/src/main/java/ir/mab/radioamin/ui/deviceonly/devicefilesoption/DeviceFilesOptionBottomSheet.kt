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
import androidx.core.os.bundleOf
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
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionAddToPlaylistOnClickListener
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.snackWithNavigateAction
import ir.mab.radioamin.vm.DeviceAlbumsViewModel
import ir.mab.radioamin.vm.DeviceArtistsViewModel
import ir.mab.radioamin.vm.DevicePlaylistsViewModel
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.DevicePlaylist
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber

@AndroidEntryPoint
class DeviceFilesOptionBottomSheet(
    private val id: Long,
    private val title: String,
    private val subtitle: String,
    private val thumbnail: Bitmap?,
    private val type: DeviceFileType
) : BottomSheetDialogFragment(), DeviceFilesOptionAddToPlaylistOnClickListener {

    private lateinit var binding: BottomsheetDeivceFilesOptionBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()
    private val deviceAlbumsViewModel: DeviceAlbumsViewModel by viewModels()
    private val deviceArtistsViewModel: DeviceArtistsViewModel by viewModels()
    private lateinit var addToPlaylistDialog: AlertDialog

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
            showAddToPlaylistDialog()
        }

    }

    private fun showAddToPlaylistDialog() {

        val addToPlaylistDialogAdapter = AddToPlaylistDialogAdapter(mutableListOf(), this)
        val dialogAddToPlaylistBinding =
            DialogAddToPlaylistBinding.inflate(LayoutInflater.from(requireContext()))

        dialogAddToPlaylistBinding.list.adapter = addToPlaylistDialogAdapter
        dialogAddToPlaylistBinding.list.layoutManager = LinearLayoutManager(requireContext())

        addToPlaylistDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogAddToPlaylistBinding.root)
            .show()

        dialogAddToPlaylistBinding.createNewPlaylistButton.setOnClickListener {
            showCreateNewPlaylistDialog()
            addToPlaylistDialog.dismiss()
        }

        dialogAddToPlaylistBinding.closeIcon.setOnClickListener {
            addToPlaylistDialog.dismiss()
        }


        devicePlaylistsViewModel.getDevicePlaylists().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    dialogAddToPlaylistBinding.showProgress = true
                }

                Status.SUCCESS -> {
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

        val dialogCreatePlaylistBinding =
            DialogCreatePlaylistBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogCreatePlaylistBinding.root)
            .show()


        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)


        dialogCreatePlaylistBinding.title.addTextChangedListener {
            if (TextUtils.isEmpty(it)) {
                dialogCreatePlaylistBinding.positiveButton.isEnabled = false
                dialogCreatePlaylistBinding.positiveButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color5
                    )
                )
            } else {
                dialogCreatePlaylistBinding.positiveButton.isEnabled = true
                dialogCreatePlaylistBinding.positiveButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
        }

        dialogCreatePlaylistBinding.positiveButton.setOnClickListener {
            createNewPlaylist(dialogCreatePlaylistBinding.title.text.toString(), dialog)
        }

        dialogCreatePlaylistBinding.negativeButton.setOnClickListener {
            dialog.dismiss()
        }


    }

    private fun createNewPlaylist(title: String, dialog: AlertDialog) {
        devicePlaylistsViewModel.createPlaylist(title).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    dialog.dismiss()
                }

                Status.ERROR -> {
                    Timber.e(it.message)
                }
            }
        })
    }

    private fun getDeviceAlbumSongsAndAddToPlaylist(albumId: Long, playlist: DevicePlaylist) {
        deviceAlbumsViewModel.getDeviceAlbumSongs(albumId).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    addSongsToPlaylist(it.data, playlist)
                }

                Status.ERROR -> {
                }
            }
        })
    }

    private fun getDevicePlaylistSongsAndAddToPlaylist(id: Long, devicePlaylist: DevicePlaylist) {
        devicePlaylistsViewModel.getDevicePlaylistMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    addSongsToPlaylist(it.data, devicePlaylist)
                }

                Status.ERROR -> {
                }
            }
        })
    }

    private fun getDeviceSongsAndAddToPlaylist(id: Long, devicePlaylist: DevicePlaylist) {
        val songs = mutableListOf(DeviceSong(id, null, null, null, null, null))
        addSongsToPlaylist(songs, devicePlaylist)
    }

    private fun getDeviceArtistSongsAndAddToPlaylist(id: Long, devicePlaylist: DevicePlaylist) {
        deviceArtistsViewModel.getDeviceArtistSongs(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    addSongsToPlaylist(it.data, devicePlaylist)
                }

                Status.ERROR -> {
                }
            }
        })
    }

    private fun addSongsToPlaylist(songs: List<DeviceSong>?, playlist: DevicePlaylist) {
        if (!songs.isNullOrEmpty()) {
            devicePlaylistsViewModel.addSongsToPlaylist(songs, playlist.id ?: -1)
                .observe(viewLifecycleOwner, {
                    Timber.d("addSongsToPlaylist %s", it)
                    when (it.status) {
                        Status.LOADING -> {

                        }

                        Status.SUCCESS -> {
                            if (it.data == true) {
                                val bundle = bundleOf(
                                    AppConstants.Arguments.PLAYLIST_ID to playlist.id,
                                    AppConstants.Arguments.PLAYLIST_NAME to playlist.name,
                                )
                                requireActivity().snackWithNavigateAction(
                                    getString(R.string.added_to_playlist),
                                    R.id.devicePlaylist,
                                    bundle
                                )

                                addToPlaylistDialog.dismiss()

                                dismiss()
                            }
                        }

                        Status.ERROR -> {
                        }
                    }
                })
        }
    }

    override fun onPlaylistClicked(devicePlaylist: DevicePlaylist) {
        when (type) {
            DeviceFileType.PLAYLIST -> {
                getDevicePlaylistSongsAndAddToPlaylist(id, devicePlaylist)
            }
            DeviceFileType.ALBUM -> {
                getDeviceAlbumSongsAndAddToPlaylist(id, devicePlaylist)
            }
            DeviceFileType.SONG -> {
                getDeviceSongsAndAddToPlaylist(id, devicePlaylist)
            }
            DeviceFileType.ARTIST -> {
                getDeviceArtistSongsAndAddToPlaylist(id, devicePlaylist)
            }
        }
    }


}