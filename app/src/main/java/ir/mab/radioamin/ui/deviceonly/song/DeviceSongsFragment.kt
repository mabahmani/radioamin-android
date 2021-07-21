package ir.mab.radioamin.ui.deviceonly.song

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentDeviceSongsBinding
import ir.mab.radioamin.ui.deviceonly.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.util.hidePermissionEducational
import ir.mab.radioamin.util.showPermissionEducational
import ir.mab.radioamin.vm.DeviceSongsViewModel
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class DeviceSongsFragment : Fragment(), DeviceFilesMoreOnClickListeners {
    private lateinit var binding: FragmentDeviceSongsBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val deviceSongsViewModel: DeviceSongsViewModel by viewModels()
    private var deviceSongsAdapter = DeviceSongsAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceSongsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.handler = Handlers()
        initRefreshLayout()
        initRequestPermissionLauncher()
        checkPermissions()
        initList()
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED -> {

                getDeviceSongs()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                binding.refreshLayout.isRefreshing = false
                binding.showEmptyList = true
                requireActivity().showPermissionEducational(
                    getString(R.string.read_file_permission_title),
                    getString(R.string.read_file_permission_description)
                ) {
                    if (it) {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    requireActivity().hidePermissionEducational()
                }
            }

            else -> {
                binding.refreshLayout.isRefreshing = false
                binding.showEmptyList = true
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun initRequestPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                //granted
                if (it == true) {
                    getDeviceSongs()
                }
            }
    }

    private fun initRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            checkPermissions()
        }
    }

    private fun initList() {
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
                }

                Status.ERROR -> {
                    binding.showProgress = false
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })

    }

    inner class Handlers {
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