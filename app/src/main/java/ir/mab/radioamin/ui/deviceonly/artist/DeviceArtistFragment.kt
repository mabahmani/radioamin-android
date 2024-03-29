package ir.mab.radioamin.ui.deviceonly.artist

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentDeviceArtistBinding
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionsChangeListener
import ir.mab.radioamin.ui.deviceonly.listener.DeviceSongsOnClickListeners
import ir.mab.radioamin.ui.deviceonly.song.DeviceSongsAdapter
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.DeviceFilesPlayer.setDeviceFilesPlayerPlaylist
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.vm.devicefiles.DeviceArtistsViewModel
import ir.mab.radioamin.vo.devicefiles.DeviceFileType
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class DeviceArtistFragment : Fragment(), DeviceFilesMoreOnClickListeners, DeviceFilesOptionsChangeListener, DeviceSongsOnClickListeners{
    private lateinit var binding: FragmentDeviceArtistBinding
    private val deviceArtistViewModel: DeviceArtistsViewModel by viewModels()
    private var deviceSongsAdapter = DeviceSongsAdapter(mutableListOf(), this, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceArtistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefreshLayout()
        initList()
        setToolbarTitle()
        getDeviceArtistSongs()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.shuffle.setOnClickListener {
            if(!deviceSongsAdapter.list.isNullOrEmpty())
                requireActivity().setDeviceFilesPlayerPlaylist(deviceSongsAdapter.list.shuffled(), 0)
        }
    }

    private fun setToolbarTitle() {
        binding.title = requireArguments().getString(AppConstants.Arguments.ARTIST_NAME, "")
    }


    private fun initRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            getDeviceArtistSongs()
        }
    }

    private fun initList() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = deviceSongsAdapter
    }

    private fun getDeviceArtistSongs() {
        deviceArtistViewModel.getDeviceArtistSongs(
            requireArguments().getLong(
                AppConstants.Arguments.ARTIST_ID,
                -1
            )
        ).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    deviceSongsAdapter.list = it.data ?: mutableListOf()
                    deviceSongsAdapter.notifyDataSetChanged()

                    binding.showProgress = false
                    binding.refreshLayout.isRefreshing = false
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                    binding.showProgress = false
                    binding.refreshLayout.isRefreshing = false
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
        getDeviceArtistSongs()
    }

    override fun onSongClick(position: Int) {
        if(!deviceSongsAdapter.list.isNullOrEmpty())
            requireActivity().setDeviceFilesPlayerPlaylist(deviceSongsAdapter.list, position)
    }

}