package ir.mab.radioamin.ui.deviceonly.song

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentDeviceSongsBinding
import ir.mab.radioamin.ui.deviceonly.DeviceFilesBaseFragment
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionsChangeListener
import ir.mab.radioamin.ui.deviceonly.listener.DeviceSongsOnClickListeners
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.DeviceFilesPlayer.setDeviceFilesPlayerPlaylist
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.util.snackWithNavigateAction
import ir.mab.radioamin.vm.DeviceSongsViewModel
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.generic.Status
import javax.inject.Inject

@AndroidEntryPoint
class DeviceSongsFragment : DeviceFilesBaseFragment(),
    DeviceFilesMoreOnClickListeners,
    DeviceFilesOptionsChangeListener,
    DeviceSongsOnClickListeners
{
    private lateinit var binding: FragmentDeviceSongsBinding
    private val deviceSongsViewModel: DeviceSongsViewModel by viewModels()
    private var deviceSongsAdapter = DeviceSongsAdapter(mutableListOf(), this, this)
    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceSongsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.handler = Handlers()
        initRefreshLayout()
        observePermissionsGranted()
        initList()
    }

    private fun notifySongsFilters() {
        if (!sharedPreferences.getStringSet(AppConstants.PREFS.BLACK_LIST_FOLDERS, mutableSetOf()).isNullOrEmpty()){
            requireActivity().snackWithNavigateAction(
                getString(R.string.filtered_songs_notify_msg),
                R.id.filterDeviceFolders,
                null
            )
        }
    }

    private fun observePermissionsGranted() {
        checkPermissions().observe(viewLifecycleOwner, {
            if (it != null && it){
                getDeviceSongs()
            }
        })
    }


    private fun initRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            observePermissionsGranted()
        }
    }

    private fun initList() {
        binding.showEmptyList = true
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = deviceSongsAdapter
    }

    private fun getDeviceSongs() {
        deviceSongsViewModel.getDeviceSongs().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.showEmptyList = true
                    } else {
                        deviceSongsAdapter.list = it.data
                        deviceSongsAdapter.notifyDataSetChanged()
                        binding.showEmptyList = false
                    }

                    binding.showProgress = false
                    binding.refreshLayout.isRefreshing = false

                    notifySongsFilters()
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                    binding.showProgress = false
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })

    }

    inner class Handlers {
        fun shuffleOnClick(v: View){
            requireActivity().setDeviceFilesPlayerPlaylist(deviceSongsAdapter.list.shuffled(), 0)
        }
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
        getDeviceSongs()
    }

    override fun onSongClick(position: Int) {
        requireActivity().setDeviceFilesPlayerPlaylist(deviceSongsAdapter.list, position)
    }

}