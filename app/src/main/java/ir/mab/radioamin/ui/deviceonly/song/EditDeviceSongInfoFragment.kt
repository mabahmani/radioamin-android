package ir.mab.radioamin.ui.deviceonly.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentEditDeviceSongInfoBinding
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.util.snack
import ir.mab.radioamin.vm.DeviceSongsViewModel
import ir.mab.radioamin.vo.DeviceSongTag
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class EditDeviceSongInfoFragment: Fragment() {
    private lateinit var binding: FragmentEditDeviceSongInfoBinding
    private val deviceSongsViewModel: DeviceSongsViewModel by viewModels()

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
        getDeviceSongTags()
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
                )
            ).observe(viewLifecycleOwner, {
                when(it.status){
                    Status.LOADING ->{

                    }

                    Status.SUCCESS ->{
                        requireActivity().snack(getString(R.string.song_info_updated))
                        findNavController().popBackStack(R.id.deviceFiles, false)
                    }

                    Status.ERROR ->{
                        requireContext().errorToast(it.message.toString())
                    }
                }
            })
        }
    }


}