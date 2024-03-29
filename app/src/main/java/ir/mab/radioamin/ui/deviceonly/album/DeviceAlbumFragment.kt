package ir.mab.radioamin.ui.deviceonly.album

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentDeviceAlbumBinding
import ir.mab.radioamin.ui.deviceonly.devicefilesoption.DeviceFilesOptionBottomSheet
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesMoreOnClickListeners
import ir.mab.radioamin.ui.deviceonly.listener.DeviceFilesOptionsChangeListener
import ir.mab.radioamin.ui.deviceonly.listener.DeviceSongsOnClickListeners
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.DeviceFilesImageLoader.getOriginalAlbumArt
import ir.mab.radioamin.util.DeviceFilesPlayer.setDeviceFilesPlayerPlaylist
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.vm.devicefiles.DeviceAlbumsViewModel
import ir.mab.radioamin.vo.devicefiles.DeviceFileType
import ir.mab.radioamin.vo.generic.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceAlbumFragment: Fragment(), DeviceFilesMoreOnClickListeners, DeviceFilesOptionsChangeListener,
    DeviceSongsOnClickListeners {
    private lateinit var binding: FragmentDeviceAlbumBinding
    private val deviceAlbumsViewModel: DeviceAlbumsViewModel by viewModels()
    private var deviceAlbumSongsAdapter = DeviceAlbumSongsAdapter(mutableListOf(), this, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceAlbumBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAppBarScroll()
        setBundleData()
        initList()
        getAlbum()
        getAlbumSongs()
        setClickListener()
    }

    private fun setClickListener() {
        binding.more.setOnClickListener {
            DeviceFilesOptionBottomSheet(
                arguments?.getLong(AppConstants.Arguments.ALBUM_ID)?: -1,
                binding.albumName?: "",
                binding.albumArtist?: "",
                binding.albumThumbnail,
                DeviceFileType.ALBUM
            ).show(requireActivity().supportFragmentManager, null)
        }

        binding.playButton.setOnClickListener {
            if(!deviceAlbumSongsAdapter.list.isNullOrEmpty())
                requireActivity().setDeviceFilesPlayerPlaylist(deviceAlbumSongsAdapter.list, 0)
        }

        binding.shuffleButton.setOnClickListener {
            if(!deviceAlbumSongsAdapter.list.isNullOrEmpty())
                requireActivity().setDeviceFilesPlayerPlaylist(deviceAlbumSongsAdapter.list.shuffled(), 0)
        }
    }

    private fun setBundleData() {
        binding.albumName = arguments?.getString(AppConstants.Arguments.ALBUM_NAME, "")
    }

    private fun observeAppBarScroll() {

        binding.toolbar.setTitleVisibility(GONE)

        var scrollRange = -1
        var scrollRangeQuarter = -1
        var isVisible = false

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            //set app bar max scroll range
            if (scrollRange == -1){
                scrollRange = barLayout?.totalScrollRange!!
                scrollRangeQuarter = scrollRange - (scrollRange / 4)
            }

            //hide toolbar title when collapsed
            if ((verticalOffset + scrollRange) < scrollRangeQuarter){
                if(!isVisible){
                    binding.toolbar.setTitleVisibilityWithAnim(VISIBLE)
                    isVisible = true
                }
            }

            //hide toolbar title when expanded
            else{
                if(isVisible){
                    binding.toolbar.setTitleVisibilityWithAnim(GONE)
                    isVisible = false
                }
            }

            //set alpha for toolbar content based on scroll offset
            binding.toolbarContent.alpha = (scrollRange + verticalOffset).toFloat() / scrollRange.toFloat()

        })
    }

    private fun initList() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = deviceAlbumSongsAdapter
    }


    private fun getAlbum() {
        deviceAlbumsViewModel.getDeviceAlbum(arguments?.getLong(AppConstants.Arguments.ALBUM_ID)?:-1).observe(viewLifecycleOwner,{
            when(it.status){
                Status.LOADING ->{
                    binding.showProgress = true
                }

                Status.SUCCESS ->{
                    if (it.data != null){
                        GlobalScope.launch(Dispatchers.IO){
                            binding.albumThumbnail = requireContext().getOriginalAlbumArt(it.data.id ?: -1)
                            deviceAlbumSongsAdapter.albumArt = binding.albumThumbnail

                        }
                        binding.albumArtist = it.data.artist
                    }

                    else{
                        findNavController().popBackStack()
                    }

                    binding.showProgress = false
                }

                Status.ERROR ->{
                    requireContext().errorToast(it.message.toString())
                    binding.showProgress = false
                }
            }
        })
    }

    private fun getAlbumSongs() {
        deviceAlbumsViewModel.getDeviceAlbumSongs(arguments?.getLong(AppConstants.Arguments.ALBUM_ID)?:-1).observe(viewLifecycleOwner,{

            when(it.status){
                Status.LOADING ->{
                    binding.showProgress = true
                }

                Status.SUCCESS ->{
                    deviceAlbumSongsAdapter.list = it.data?: mutableListOf()
                    deviceAlbumSongsAdapter.notifyDataSetChanged()
                    binding.showProgress = false
                }

                Status.ERROR ->{
                    requireContext().errorToast(it.message.toString())
                    binding.showProgress = false
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
            binding.albumThumbnail,
            type,
            this
        ).show(requireActivity().supportFragmentManager, null)
    }

    override fun onDeviceFilesChanged() {
        getAlbum()
        getAlbumSongs()
    }

    override fun onSongClick(position: Int) {
        if(!deviceAlbumSongsAdapter.list.isNullOrEmpty())
            requireActivity().setDeviceFilesPlayerPlaylist(deviceAlbumSongsAdapter.list, position)
    }

}