package ir.mab.radioamin.ui.deviceonly.song

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentEditDeviceSongInfoBinding
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.DeviceFilesImageLoader.getOriginalAlbumArt
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.util.snack
import ir.mab.radioamin.vm.DeviceSongsViewModel
import ir.mab.radioamin.vo.DeviceSongTag
import ir.mab.radioamin.vo.generic.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditDeviceSongInfoFragment: Fragment() {
    private lateinit var binding: FragmentEditDeviceSongInfoBinding
    private val deviceSongsViewModel: DeviceSongsViewModel by viewModels()
    private var coverArtUri:Uri? = null

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null && data.data != null){
                coverArtUri = result.data?.data
                binding.coverImageView.setImageURI(data.data)
                binding.coverEditMode = false
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditDeviceSongInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.handler = Handlers()
        binding.coverEditMode = true
        getDeviceSongTags()
        getDeviceSongCover()
    }

    private fun getDeviceSongCover() {
        GlobalScope.launch(Dispatchers.IO) {
            binding.cover = requireContext().getOriginalAlbumArt(requireArguments().getLong(AppConstants.Arguments.ALBUM_ID, -1))
        }
    }

    private fun getDeviceSongTags() {
        deviceSongsViewModel.getDeviceSongTags(requireArguments().getString(AppConstants.Arguments.SONG_DATA, "")).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    binding.title = it.data?.title
                    binding.album = it.data?.album
                    binding.artist = it.data?.artist
                    binding.genre = it.data?.genre
                    binding.language = it.data?.language
                    binding.country = it.data?.country
                    binding.year = it.data?.year
                    binding.lyrics = it.data?.lyrics
                    binding.showProgress = false
                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                    binding.showProgress = false
                }
            }
        })

    }

    inner class Handlers {
        fun onClickDone(view: View) {
            deviceSongsViewModel.writeDeviceSongTags(
                requireArguments().getString(AppConstants.Arguments.SONG_DATA, ""),
                DeviceSongTag(
                    binding.title,
                    binding.album,
                    binding.artist,
                    binding.genre,
                    binding.language,
                    binding.country,
                    binding.year,
                    binding.lyrics
                ),
                coverArtUri
            ).observe(viewLifecycleOwner, {
                when(it.status){
                    Status.LOADING ->{

                    }

                    Status.SUCCESS ->{
                        requireActivity().snack(getString(R.string.song_info_updated))
                        findNavController().popBackStack()
                    }

                    Status.ERROR ->{
                        requireContext().errorToast(it.message.toString())
                    }
                }
            })
        }

        fun onClickCover(view: View) {
            resultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
    }


}