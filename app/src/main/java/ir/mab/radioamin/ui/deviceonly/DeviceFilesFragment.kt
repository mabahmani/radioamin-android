package ir.mab.radioamin.ui.deviceonly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentDeviceFilesBinding

class DeviceFilesFragment: Fragment() {
    lateinit var binding: FragmentDeviceFilesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceFilesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
        }

        binding.playlistsParent.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_deviceFilesFragment_to_devicePlaylistsFragment)
        }
    }
}