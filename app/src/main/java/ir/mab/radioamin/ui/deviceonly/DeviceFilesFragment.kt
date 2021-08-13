package ir.mab.radioamin.ui.deviceonly

import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentDeviceFilesBinding
import ir.mab.radioamin.util.toast

class DeviceFilesFragment : Fragment() {
    lateinit var binding: FragmentDeviceFilesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceFilesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {

        binding.refreshLayout.setOnRefreshListener {

            requireContext().toast(getString(R.string.scanning_media_files))

            MediaScannerConnection.scanFile(requireContext(), arrayOf(Environment.getExternalStorageDirectory().path), null) { s, uri ->
                binding.refreshLayout.isRefreshing = false
            }
        }

        binding.playlistsParent.setOnClickListener {
            it.findNavController().navigate(R.id.action_deviceFilesFragment_to_devicePlaylistsFragment)
        }

        binding.albumsParent.setOnClickListener {
            it.findNavController().navigate(R.id.action_deviceFilesFragment_to_deviceAlbumsFragment)
        }

        binding.songsParent.setOnClickListener {
            it.findNavController().navigate(R.id.action_deviceFilesFragment_to_deviceSongsFragment)
        }

        binding.artistsParent.setOnClickListener {
            it.findNavController().navigate(R.id.action_deviceFilesFragment_to_deviceArtistsFragment)
        }

        binding.genresParent.setOnClickListener {
            it.findNavController().navigate(R.id.action_deviceFilesFragment_to_deviceGenresFragment)
        }
    }
}