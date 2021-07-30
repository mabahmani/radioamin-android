package ir.mab.radioamin.ui.deviceonly.album

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentDeviceAlbumsBinding
import ir.mab.radioamin.ui.deviceonly.DeviceFilesBaseFragment
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.vm.DeviceAlbumsViewModel
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class DeviceAlbumsFragment : DeviceFilesBaseFragment(), DeviceFilesMoreOnClickListeners {
    lateinit var binding: FragmentDeviceAlbumsBinding
    private val deviceAlbumsViewModel: DeviceAlbumsViewModel by viewModels()
    var deviceAlbumsAdapter = DeviceAlbumsAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceAlbumsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefreshLayout()
        observePermissionsGranted()
        initList()
    }

    private fun initRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            observePermissionsGranted()
        }
    }

    private fun observePermissionsGranted() {
        checkPermissions().observe(viewLifecycleOwner, {
            if (it != null && it){
                getDeviceAlbums()
            }
        })
    }

    private fun initList() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = deviceAlbumsAdapter
    }

    private fun getDeviceAlbums() {
        deviceAlbumsViewModel.getDeviceAlbums().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.showEmptyList = true
                    } else {
                        deviceAlbumsAdapter.list = it.data
                        deviceAlbumsAdapter.notifyDataSetChanged()
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