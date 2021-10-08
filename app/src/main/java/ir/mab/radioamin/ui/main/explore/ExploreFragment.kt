package ir.mab.radioamin.ui.main.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentExploreBinding
import ir.mab.radioamin.vm.remote.MainViewModel
import ir.mab.radioamin.vo.generic.Status

@AndroidEntryPoint
class ExploreFragment: Fragment() {

    lateinit var binding: FragmentExploreBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeRefreshLayout()
        getMusics(false)
        getAlbums(false)
        getArtists(false)
        getGenres(false)
        getMusicVideos(false)
        getPlaylists(false)
    }

    private fun observeRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            getMusics(true)
            getAlbums(true)
            getArtists(true)
            getGenres(true)
            getMusicVideos(true)
            getPlaylists(true)
        }
    }

    private fun getMusics(forceRefresh: Boolean) {
        mainViewModel.getMusics(forceRefresh).observe(viewLifecycleOwner, {

            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    binding.refreshLayout.isRefreshing = false
                    if (it.data?.data != null){

                        binding.musicList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.musicList.adapter = ExploreMusicAdapter(it.data.data.content)
                    }

                }

                Status.ERROR ->{
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun getAlbums(forceRefresh: Boolean) {
        mainViewModel.getAlbums(forceRefresh).observe(viewLifecycleOwner, {

            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    binding.refreshLayout.isRefreshing = false
                    if (it.data?.data != null){

                        binding.albumList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.albumList.adapter = ExploreAlbumAdapter(it.data.data.content)
                    }

                }

                Status.ERROR ->{
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun getArtists(forceRefresh: Boolean) {
        mainViewModel.getArtists(forceRefresh).observe(viewLifecycleOwner, {

            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    binding.refreshLayout.isRefreshing = false
                    if (it.data?.data != null){

                        binding.artistList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.artistList.adapter = ExploreSingerAdapter(it.data.data.content)
                    }

                }

                Status.ERROR ->{
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun getGenres(forceRefresh: Boolean) {
        mainViewModel.getGenres(forceRefresh).observe(viewLifecycleOwner, {

            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    binding.refreshLayout.isRefreshing = false
                    if (it.data?.data != null){

                        binding.genresList.layoutManager = GridLayoutManager(requireContext(),3, LinearLayoutManager.HORIZONTAL, false)
                        binding.genresList.adapter = ExploreGenreAdapter(it.data.data.content)
                    }

                }

                Status.ERROR ->{
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun getPlaylists(forceRefresh: Boolean) {
        mainViewModel.getPlaylists(forceRefresh).observe(viewLifecycleOwner, {

            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    binding.refreshLayout.isRefreshing = false
                    if (it.data?.data != null){

                        binding.playlistsList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.playlistsList.adapter = ExplorePlaylistAdapter(it.data.data.content)
                    }

                }

                Status.ERROR ->{
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun getMusicVideos(forceRefresh: Boolean) {
        mainViewModel.getMusicVideos(forceRefresh).observe(viewLifecycleOwner, {

            when(it.status){
                Status.LOADING ->{

                }

                Status.SUCCESS ->{
                    binding.refreshLayout.isRefreshing = false
                    if (it.data?.data != null){

                        binding.musicVideosList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.musicVideosList.adapter = ExploreMusicVideoAdapter(it.data.data.content)
                    }

                }

                Status.ERROR ->{
                    binding.refreshLayout.isRefreshing = false
                }
            }
        })
    }
}