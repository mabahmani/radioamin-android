package ir.mab.radioamin.ui.deviceonly.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentDeviceAlbumBinding
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vm.DeviceAlbumsViewModel
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber

@AndroidEntryPoint
class DeviceAlbumFragment: Fragment() {
    private lateinit var binding: FragmentDeviceAlbumBinding
    private val deviceAlbumsViewModel: DeviceAlbumsViewModel by viewModels()
    private var deviceAlbumSongsAdapter = DeviceAlbumSongsAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    }

    private fun setBundleData() {
        binding.albumName = arguments?.getString(AppConstants.Arguments.ALBUM_NAME)
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
            Timber.d("getAlbum %s" , it)

            when(it.status){
                Status.LOADING ->{
                    binding.showProgress = true
                }

                Status.SUCCESS ->{
                    if (it.data != null){
                        if(it.data.thumbnail != null){
                            binding.albumThumbnail = it.data.thumbnail
                        }
                        binding.albumArtist = it.data.artist
                    }

                    binding.showProgress = false
                }

                Status.ERROR ->{
                    binding.showProgress = false
                }
            }
        })
    }

    private fun getAlbumSongs() {
        deviceAlbumsViewModel.getDeviceAlbumSongs(arguments?.getLong(AppConstants.Arguments.ALBUM_ID)?:-1).observe(viewLifecycleOwner,{
            Timber.d("getAlbumSongs %s" , it)

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
                    binding.showProgress = false
                }
            }
        })
    }

}