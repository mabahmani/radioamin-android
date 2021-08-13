package ir.mab.radioamin.ui.deviceonly.playlist

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.DialogCreatePlaylistBinding
import ir.mab.radioamin.databinding.FragmentDevicePlaylistsBinding
import ir.mab.radioamin.ui.deviceonly.DeviceFilesBaseFragment
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionsChangeListener
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.util.snack
import ir.mab.radioamin.vm.DevicePlaylistsViewModel
import ir.mab.radioamin.vo.DeviceFileType
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber

@AndroidEntryPoint
class DevicePlaylistsFragment : DeviceFilesBaseFragment(), DeviceFilesMoreOnClickListeners, DeviceFilesOptionsChangeListener {
    private lateinit var binding: FragmentDevicePlaylistsBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()
    private var devicePlaylistsAdapter = DevicePlaylistsAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDevicePlaylistsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.handler = Handlers()
        initRefreshLayout()
        observePermissionsGranted()
        initList()
    }

    private fun observePermissionsGranted() {
        checkPermissions().observe(viewLifecycleOwner, {
            if (it != null && it){
                getDevicePlaylists()
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
        binding.list.adapter = devicePlaylistsAdapter
    }

    private fun getDevicePlaylists() {
        devicePlaylistsViewModel.getDevicePlaylists().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.showEmptyList = true
                    } else {
                        devicePlaylistsAdapter.list = it.data
                        devicePlaylistsAdapter.notifyDataSetChanged()
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

    private fun createNewPlaylist(title: String, dialog: AlertDialog) {
        devicePlaylistsViewModel.createPlaylist(title).observe(viewLifecycleOwner,{
            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    getDevicePlaylists()
                    requireActivity().snack(
                        getString(R.string.playlist_creation_msg).format(title)
                    )
                    dialog.dismiss()
                }

                Status.ERROR ->{
                    requireContext().errorToast(it.message.toString())
                    requireContext().errorToast(it.message.toString())
                    Timber.e(it.message)
                }
            }
        })
    }

    inner class Handlers {
        fun onClickNewPlaylist(view: View){

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
        getDevicePlaylists()
    }

}