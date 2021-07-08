package ir.mab.radioamin.ui.deviceonly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentDevicePlaylistsBinding
import ir.mab.radioamin.vm.DevicePlaylistsViewModel
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class DevicePlaylistsFragment: Fragment() {
    lateinit var binding: FragmentDevicePlaylistsBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()
    var devicePlaylistsAdapter = DevicePlaylistsAdapter(mutableListOf())

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
        initList()
        getDevicePlaylists()
    }

    private fun initList() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = devicePlaylistsAdapter
    }

    private fun getDevicePlaylists() {
        devicePlaylistsViewModel.getDevicePlaylists().observe(viewLifecycleOwner,{
            when(it.status){
                Status.LOADING ->{
                    binding.showProgress = true
                }

                Status.SUCCESS ->{
                    devicePlaylistsAdapter.list = it.data?: mutableListOf()
                    devicePlaylistsAdapter.notifyDataSetChanged()
                    binding.showProgress = false
                }

                Status.ERROR ->{
                    binding.showProgress = false
                }
            }
        })

    }


    inner class Handlers {

    }
}