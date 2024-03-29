package ir.mab.radioamin.ui.deviceonly.playlist

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentDevicePlaylistBinding
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionsChangeListener
import ir.mab.radioamin.ui.deviceonly.listener.DeviceSongsOnClickListeners
import ir.mab.radioamin.ui.deviceonly.song.DeviceSongsAdapter
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.DeviceFilesImageLoader.getOriginalAlbumArt
import ir.mab.radioamin.util.DeviceFilesPlayer.setDeviceFilesPlayerPlaylist
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.vm.devicefiles.DevicePlaylistsViewModel
import ir.mab.radioamin.vo.devicefiles.DeviceFileType
import ir.mab.radioamin.vo.generic.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DevicePlaylistFragment : Fragment(), DeviceFilesMoreOnClickListeners,
    DeviceFilesOptionsChangeListener, DeviceSongsOnClickListeners {
    private lateinit var binding: FragmentDevicePlaylistBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()
    private var deviceSongsAdapter = DeviceSongsAdapter(mutableListOf(), this, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDevicePlaylistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAppBarScroll()
        setBundleData()
        initList()
        getDevicePlaylistMembers()
        setClickListener()
    }

    private fun setClickListener() {
        binding.more.setOnClickListener {
            DeviceFilesOptionBottomSheet(
                arguments?.getLong(AppConstants.Arguments.PLAYLIST_ID) ?: -1,
                binding.playlistName ?: "",
                binding.playlistCountsText.text.toString(),
                binding.playlistThumbnail,
                DeviceFileType.PLAYLIST,
                this
            ).show(requireActivity().supportFragmentManager, null)
        }

        binding.edit.setOnClickListener {
            val bundle = bundleOf(
                AppConstants.Arguments.PLAYLIST_ID to arguments?.getLong(AppConstants.Arguments.PLAYLIST_ID),
                AppConstants.Arguments.PLAYLIST_NAME to binding.playlistName
            )
            it.findNavController()
                .navigate(R.id.action_devicePlaylistFragment_to_editDevicePlaylistFragment, bundle)
        }

        binding.playButton.setOnClickListener {
            if(!deviceSongsAdapter.list.isNullOrEmpty())
                requireActivity().setDeviceFilesPlayerPlaylist(deviceSongsAdapter.list, 0)
        }

        binding.shuffleButton.setOnClickListener {
            if(!deviceSongsAdapter.list.isNullOrEmpty())
                requireActivity().setDeviceFilesPlayerPlaylist(deviceSongsAdapter.list.shuffled(), 0)
        }
    }

    private fun setBundleData() {
        binding.playlistName = arguments?.getString(AppConstants.Arguments.PLAYLIST_NAME, "")
    }

    private fun observeAppBarScroll() {

        binding.toolbar.setTitleVisibility(GONE)

        var scrollRange = -1
        var scrollRangeQuarter = -1
        var isVisible = false

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            //set app bar max scroll range
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
                scrollRangeQuarter = scrollRange - (scrollRange / 4)
            }

            //hide toolbar title when collapsed
            if ((verticalOffset + scrollRange) < scrollRangeQuarter) {
                if (!isVisible) {
                    binding.toolbar.setTitleVisibilityWithAnim(VISIBLE)
                    isVisible = true
                }
            }

            //hide toolbar title when expanded
            else {
                if (isVisible) {
                    binding.toolbar.setTitleVisibilityWithAnim(GONE)
                    isVisible = false
                }
            }

            //set alpha for toolbar content based on scroll offset
            binding.toolbarContent.alpha =
                (scrollRange + verticalOffset).toFloat() / scrollRange.toFloat()

        })
    }

    private fun initList() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = deviceSongsAdapter
    }

    private fun getDevicePlaylistMembers() {
        devicePlaylistsViewModel.getDevicePlaylistMembers(
            arguments?.getLong(AppConstants.Arguments.PLAYLIST_ID) ?: -1
        ).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.playlistMembersCount = 0
                    } else {
                        GlobalScope.launch(Dispatchers.IO){
                            binding.playlistThumbnail = requireContext().getOriginalAlbumArt(it.data[0].albumId?: -1)
                        }
                        binding.playlistMembersCount = it.data.size
                        deviceSongsAdapter.list = it.data
                        deviceSongsAdapter.notifyDataSetChanged()
                    }
                    binding.showProgress = false
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                    binding.showProgress = false
                }
            }
        })
    }

    override fun onShowOptions(
        id: Long,
        title: String,
        subtitle: String,
        thumbnail: Bitmap?,
        type: DeviceFileType
    ) {
        DeviceFilesOptionBottomSheet(
            id,
            title,
            subtitle,
            thumbnail,
            type,
            this
        ).show(requireActivity().supportFragmentManager, null)
    }

    override fun onDeviceFilesChanged() {
        findNavController().popBackStack()
    }

    override fun onSongClick(position: Int) {
        if(!deviceSongsAdapter.list.isNullOrEmpty())
            requireActivity().setDeviceFilesPlayerPlaylist(deviceSongsAdapter.list, position)
    }
}