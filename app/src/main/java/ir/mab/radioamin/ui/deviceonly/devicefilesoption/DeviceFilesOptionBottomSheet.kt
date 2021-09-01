package ir.mab.radioamin.ui.deviceonly.devicefilesoption

import android.graphics.Bitmap
import android.net.Uri
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.*
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionAddToPlaylistOnClickListener
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionsChangeListener
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.DeviceFilesPlayer.addDeviceFilesToPlayNext
import ir.mab.radioamin.util.DeviceFilesPlayer.addDeviceFilesToPlayerQueue
import ir.mab.radioamin.util.DeviceFilesPlayer.setDeviceFilesPlayerPlaylist
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.util.snack
import ir.mab.radioamin.util.snackWithNavigateAction
import ir.mab.radioamin.vm.*
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
    private val type: DeviceFileType,
) : BottomSheetDialogFragment(), DeviceFilesOptionAddToPlaylistOnClickListener {

    private lateinit var binding: BottomsheetDeivceFilesOptionBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()
    private val deviceAlbumsViewModel: DeviceAlbumsViewModel by viewModels()
    private val deviceArtistsViewModel: DeviceArtistsViewModel by viewModels()
    private val deviceSongsViewModel: DeviceSongsViewModel by viewModels()
    private val deviceGenresViewModel: DeviceGenresViewModel by viewModels()
    private lateinit var addToPlaylistDialog: AlertDialog
    private var  deviceFilesOptionsChangeListener: DeviceFilesOptionsChangeListener? = null

    constructor(
        id: Long,
        title: String,
        subtitle: String,
        thumbnail: Bitmap?,
        type: DeviceFileType,
        deviceFilesOptionsChangeListener: DeviceFilesOptionsChangeListener
    ) : this(id, title, subtitle, thumbnail, type) {
        this.deviceFilesOptionsChangeListener = deviceFilesOptionsChangeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        binding.editPlaylistParent.setOnClickListener {
            val bundle = bundleOf(
                AppConstants.Arguments.PLAYLIST_ID to id,
                AppConstants.Arguments.PLAYLIST_NAME to title
            )
            findNavController().navigate(
                R.id.action_devicePlaylistFragment_to_editDevicePlaylistFragment,
                bundle
            )
            dismiss()
        }

        binding.deletePlaylistParent.setOnClickListener {
            showDeletePlaylistDialog()
        }

        binding.deleteSongParent.setOnClickListener {
            showDeleteSongDialog()
        }

        binding.editSongInfoParent.setOnClickListener {
            getDeviceSongAndNavigateToEditFragment()
        }

        binding.shuffleParent.setOnClickListener {
            shuffleCollection()
        }

        binding.addToQueueParent.setOnClickListener {
            addCollectionToQueue()
        }

        binding.playNextParent.setOnClickListener {
            addCollectionToPlayNext()
        }
    }

    private fun addCollectionToPlayNext() {
        when(type){
            DeviceFileType.PLAYLIST ->{
                getDevicePlaylistSongsAndPlayNext()
            }

            DeviceFileType.ALBUM ->{
                getDeviceAlbumSongsAndPlayNext()
            }

            DeviceFileType.ARTIST ->{
                getDeviceArtistSongsAndPlayNext()
            }

            DeviceFileType.GENRE ->{
                getDeviceGenreSongsAndPlayNext()
            }

            DeviceFileType.SONG ->{
                getDeviceSongAndPlayNext()
            }
        }
    }

    private fun getDevicePlaylistSongsAndPlayNext() {
        devicePlaylistsViewModel.getDevicePlaylistMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayNext(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceAlbumSongsAndPlayNext() {
        deviceAlbumsViewModel.getDeviceAlbumSongs(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayNext(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceArtistSongsAndPlayNext() {
        deviceArtistsViewModel.getDeviceArtistSongs(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayNext(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceGenreSongsAndPlayNext() {
        deviceGenresViewModel.getDeviceGenreMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayNext(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceSongAndPlayNext() {
        deviceSongsViewModel.getDeviceSong(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (it.data != null)
                        requireActivity().addDeviceFilesToPlayNext(mutableListOf(it.data))
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun addCollectionToQueue() {
        when(type){
            DeviceFileType.PLAYLIST ->{
                getDevicePlaylistSongsAndAddToQueue()
            }

            DeviceFileType.ALBUM ->{
                getDeviceAlbumSongsAndAddToQueue()
            }

            DeviceFileType.ARTIST ->{
                getDeviceArtistSongsAndAddToQueue()
            }

            DeviceFileType.GENRE ->{
                getDeviceGenreSongsAndAddToQueue()
            }

            DeviceFileType.SONG ->{
                getDeviceSongAndAddToQueue()
            }
        }
    }

    private fun getDevicePlaylistSongsAndAddToQueue() {
        devicePlaylistsViewModel.getDevicePlaylistMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayerQueue(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceAlbumSongsAndAddToQueue() {
        deviceAlbumsViewModel.getDeviceAlbumSongs(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayerQueue(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceArtistSongsAndAddToQueue() {
        deviceArtistsViewModel.getDeviceArtistSongs(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayerQueue(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceGenreSongsAndAddToQueue() {
        deviceGenresViewModel.getDeviceGenreMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().addDeviceFilesToPlayerQueue(it.data)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceSongAndAddToQueue() {
        deviceSongsViewModel.getDeviceSong(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (it.data != null)
                        requireActivity().addDeviceFilesToPlayerQueue(mutableListOf(it.data))
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun shuffleCollection() {
        when(type){
            DeviceFileType.PLAYLIST ->{
                getDevicePlaylistSongsAndShuffle()
            }

            DeviceFileType.ALBUM ->{
                getDeviceAlbumSongsAndShuffle()
            }

            DeviceFileType.ARTIST ->{
                getDeviceArtistSongsAndShuffle()
            }

            DeviceFileType.GENRE ->{
                getDeviceGenreSongsAndShuffle()
            }

            DeviceFileType.SONG ->{

            }
        }
    }

    private fun getDevicePlaylistSongsAndShuffle() {
        devicePlaylistsViewModel.getDevicePlaylistMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().setDeviceFilesPlayerPlaylist(it.data.shuffled(),0)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceAlbumSongsAndShuffle() {
        deviceAlbumsViewModel.getDeviceAlbumSongs(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().setDeviceFilesPlayerPlaylist(it.data.shuffled(),0)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceArtistSongsAndShuffle() {
        deviceArtistsViewModel.getDeviceArtistSongs(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().setDeviceFilesPlayerPlaylist(it.data.shuffled(),0)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceGenreSongsAndShuffle() {
        deviceGenresViewModel.getDeviceGenreMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty())
                        requireActivity().setDeviceFilesPlayerPlaylist(it.data.shuffled(),0)
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }

        })
    }

    private fun getDeviceSongAndNavigateToEditFragment() {
        deviceSongsViewModel.getDeviceSong(id).observe(viewLifecycleOwner, {
            when(it.status){
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    val bundle = bundleOf(
                        AppConstants.Arguments.SONG_DATA to it.data?.data,
                        AppConstants.Arguments.ALBUM_ID to it.data?.albumId,
                    )
                    findNavController().navigate(
                        R.id.action_global_editDeviceSongInfo,
                        bundle
                    )
                    dismiss()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }
        })

    }

    private fun showDeletePlaylistDialog() {

        val dialogDeletePlaylistBinding =
            DialogDeletePlaylistBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogDeletePlaylistBinding.root)
            .show()

        dialogDeletePlaylistBinding.positiveButton.setOnClickListener {
            devicePlaylistsViewModel.deletePlaylist(id).observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {
                        if (it.data == true) {
                            if (deviceFilesOptionsChangeListener != null){
                                deviceFilesOptionsChangeListener!!.onDeviceFilesChanged()
                            }
                            dialog.dismiss()
                            dismiss()

                            requireActivity().snack(getString(R.string.playlist_delete_msg).format(title))
                        }
                    }

                    Status.ERROR -> {
                        requireContext().errorToast(it.message.toString())
                    }
                }
            })
        }

        dialogDeletePlaylistBinding.negativeButton.setOnClickListener {
            dialog.dismiss()
        }


    }

    private fun showDeleteSongDialog() {

        val dialogDeleteSongBinding =
            DialogDeleteSongBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogDeleteSongBinding.root)
            .show()

        dialogDeleteSongBinding.positiveButton.setOnClickListener {
            deviceSongsViewModel.deleteDeviceSong(id).observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {
                        if (it.data == true) {
                            if (deviceFilesOptionsChangeListener != null){
                                deviceFilesOptionsChangeListener!!.onDeviceFilesChanged()
                            }
                            dialog.dismiss()
                            dismiss()

                            requireActivity().snack(getString(R.string.song_delete_msg).format(title))
                        }
                    }

                    Status.ERROR -> {
                        requireContext().errorToast(it.message.toString())
                    }
                }
            })
        }

        dialogDeleteSongBinding.negativeButton.setOnClickListener {
            dialog.dismiss()
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
                    requireContext().errorToast(it.message.toString())
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

                    if (deviceFilesOptionsChangeListener != null){
                        deviceFilesOptionsChangeListener!!.onDeviceFilesChanged()
                    }
                    dialog.dismiss()
                    dismiss()

                    requireActivity().snack(
                        getString(R.string.playlist_creation_msg).format(title)
                    )

                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
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
                    requireContext().errorToast(it.message.toString())
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
                    requireContext().errorToast(it.message.toString())
                }
            }
        })
    }

    private fun getDeviceSongsAndAddToPlaylist(id: Long, devicePlaylist: DevicePlaylist) {
        val songs = mutableListOf(DeviceSong(id, null, null, null, null, null, Uri.EMPTY, null))
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
                    requireContext().errorToast(it.message.toString())
                }
            }
        })
    }

    private fun getDeviceGenreSongsAndAddToPlaylist(id: Long, devicePlaylist: DevicePlaylist) {
        deviceGenresViewModel.getDeviceGenreMembers(id).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    addSongsToPlaylist(it.data, devicePlaylist)
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                }
            }
        })
    }

    private fun addSongsToPlaylist(songs: List<DeviceSong>?, playlist: DevicePlaylist) {
        if (!songs.isNullOrEmpty()) {
            devicePlaylistsViewModel.addSongsToPlaylist(songs, playlist.id ?: -1)
                .observe(viewLifecycleOwner, {
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
                            requireContext().errorToast(it.message.toString())
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
            DeviceFileType.GENRE -> {
                getDeviceGenreSongsAndAddToPlaylist(id, devicePlaylist)
            }
        }
    }


}