package ir.mab.radioamin.ui.deviceonly

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
import ir.mab.radioamin.databinding.FragmentDevicePlaylistBinding
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vm.DevicePlaylistsViewModel
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber

@AndroidEntryPoint
class DevicePlaylistFragment: Fragment() {
    lateinit var binding: FragmentDevicePlaylistBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()
    var deviceSongsAdapter = DeviceSongsAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDevicePlaylistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAppBarScroll()

//        binding.handler = Handlers()
        setBundleData()
        initList()
        getDevicePlaylistMembers()
    }

    private fun setBundleData() {
        binding.playlistName = arguments?.getString(AppConstants.Arguments.PLAYLIST_NAME)
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
        binding.list.adapter = deviceSongsAdapter
    }

    private fun getDevicePlaylistMembers() {
        devicePlaylistsViewModel.getDevicePlaylistMembers(arguments?.getLong(AppConstants.Arguments.PLAYLIST_ID)?:-1).observe(viewLifecycleOwner,{
            Timber.d("getDevicePlaylistMembers %s" , it)

            when(it.status){
                Status.LOADING ->{
                    binding.showProgress = true
                }

                Status.SUCCESS ->{
                    if (it.data.isNullOrEmpty()){
                        binding.playlistMembersCount = 0
                    }
                    else{
                        binding.playlistMembersCount = it.data?.size
                        binding.playlistThumbnail = it.data[0].thumbnail
                        deviceSongsAdapter.list = it.data
                        deviceSongsAdapter.notifyDataSetChanged()
                    }
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