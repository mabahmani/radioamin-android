package ir.mab.radioamin.ui.deviceonly.genre

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentDeviceGenresBinding
import ir.mab.radioamin.ui.deviceonly.DeviceFilesBaseFragment
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionsChangeListener
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.vm.devicefiles.DeviceGenresViewModel
import ir.mab.radioamin.vo.devicefiles.DeviceFileType
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class DeviceGenresFragment : DeviceFilesBaseFragment(), DeviceFilesMoreOnClickListeners, DeviceFilesOptionsChangeListener {
    private lateinit var binding: FragmentDeviceGenresBinding
    private val deviceGenreViewModel: DeviceGenresViewModel by viewModels()
    private var deviceGenresAdapter = DeviceGenresAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceGenresBinding.inflate(inflater)
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
                getDeviceGenres()
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
        binding.list.adapter = deviceGenresAdapter
    }

    private fun getDeviceGenres() {
        deviceGenreViewModel.getDeviceGenres().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.showEmptyList = true
                    } else {
                        deviceGenresAdapter.list = it.data
                        deviceGenresAdapter.notifyDataSetChanged()
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
            type,
            this
        ).show(requireActivity().supportFragmentManager, null)
    }

    override fun onDeviceFilesChanged() {
        getDeviceGenres()
    }

}