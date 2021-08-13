package ir.mab.radioamin.ui.deviceonly.artist

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentDeviceArtistsBinding
import ir.mab.radioamin.ui.deviceonly.DeviceFilesBaseFragment
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.vm.DeviceArtistsViewModel
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class DeviceArtistsFragment : DeviceFilesBaseFragment(), DeviceFilesMoreOnClickListeners {
    lateinit var binding: FragmentDeviceArtistsBinding
    private val deviceArtistsViewModel: DeviceArtistsViewModel by viewModels()
    var deviceArtistsAdapter = DeviceArtistsAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceArtistsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefreshLayout()
        observePermissionsGranted()
        initList()
    }

    private fun observePermissionsGranted() {
        checkPermissions().observe(viewLifecycleOwner, {
            if (it != null && it){
                getDeviceArtists()
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
        binding.list.adapter = deviceArtistsAdapter
    }

    private fun getDeviceArtists() {
        deviceArtistsViewModel.getDeviceArtists().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.showEmptyList = true
                    } else {
                        deviceArtistsAdapter.list = it.data
                        deviceArtistsAdapter.notifyDataSetChanged()
                        binding.showEmptyList = false
                    }

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
            type
        ).show(requireActivity().supportFragmentManager, null)
    }
}