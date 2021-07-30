package ir.mab.radioamin.ui.deviceonly.playlist

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentEditDevicePlaylistBinding
import ir.mab.radioamin.ui.deviceonly.listener.EditDevicePlaylistItemDragListeners
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.vm.DevicePlaylistsViewModel
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class EditDevicePlaylistFragment : Fragment(), EditDevicePlaylistItemDragListeners {
    private lateinit var binding: FragmentEditDevicePlaylistBinding
    private val devicePlaylistsViewModel: DevicePlaylistsViewModel by viewModels()
    private var editPlaylistSongsAdapter = EditPlaylistSongsAdapter(mutableListOf(), this)
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditDevicePlaylistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAppBarScroll()
        observeTitleChange()
        setBundleData()
        initList()
        getDevicePlaylistMembers()
        setClickListeners()
    }

    private fun observeTitleChange() {
        binding.title.addTextChangedListener {
            if (TextUtils.isEmpty(it)) {
                binding.done.isEnabled = false
                binding.done.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color5
                    )
                )
            }

            else{
                binding.done.isEnabled = true
                binding.done.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
        }
    }

    private fun setClickListeners() {
        binding.done.setOnClickListener {
            devicePlaylistsViewModel.editPlaylist(
                binding.title.text.toString(),
                editPlaylistSongsAdapter.list,
                arguments?.getLong(AppConstants.Arguments.PLAYLIST_ID) ?: -1,
                (arguments?.getString(AppConstants.Arguments.PLAYLIST_NAME)
                    ?: "") != binding.title.text.toString()
            ).observe(viewLifecycleOwner, {
                Timber.d("editPlaylist %s", it)
                when (it.status) {
                    Status.LOADING -> {

                    }
                    Status.SUCCESS -> {
                        findNavController().popBackStack(R.id.devicePlaylists, false)
                    }

                    Status.ERROR -> {

                    }
                }
            })
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    private fun setBundleData() {
        binding.playlistName = arguments?.getString(AppConstants.Arguments.PLAYLIST_NAME, "")
        binding.title.setText(arguments?.getString(AppConstants.Arguments.PLAYLIST_NAME, ""))
        binding.title.setSelection(binding.title.text.toString().length)
        binding.title.requestFocus()
    }

    private fun observeAppBarScroll() {

        binding.toolbar.setTitleVisibility(GONE)

        var scrollRange = -1
        var scrollRangeQuarter = -1
        var isVisible = false

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            //set app bar max scroll range
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
                scrollRangeQuarter = scrollRange - (scrollRange / 4)
            }

            //hide toolbar title when collapsed
            if ((verticalOffset + scrollRange) < scrollRangeQuarter) {
                if (!isVisible) {
                    binding.toolbar.setTitleVisibilityWithAnim(VISIBLE)
                    isVisible = true
                }
            }

            //hide toolbar title when expanded
            else {
                if (isVisible) {
                    binding.toolbar.setTitleVisibilityWithAnim(GONE)
                    isVisible = false
                }
            }

            //set alpha for toolbar content based on scroll offset
            binding.toolbarContent.alpha =
                (scrollRange + verticalOffset).toFloat() / scrollRange.toFloat()

        })
    }

    private fun initList() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = editPlaylistSongsAdapter

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Collections.swap(
                    editPlaylistSongsAdapter.list,
                    source.adapterPosition,
                    target.adapterPosition
                )
                editPlaylistSongsAdapter.notifyItemMoved(
                    source.adapterPosition,
                    target.adapterPosition
                )

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.list)
    }

    private fun getDevicePlaylistMembers() {
        devicePlaylistsViewModel.getDevicePlaylistMembers(
            arguments?.getLong(AppConstants.Arguments.PLAYLIST_ID) ?: -1
        ).observe(viewLifecycleOwner, {

            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    editPlaylistSongsAdapter.list = it.data ?: mutableListOf()
                    editPlaylistSongsAdapter.notifyDataSetChanged()
                    binding.showProgress = false
                }

                Status.ERROR -> {
                    binding.showProgress = false
                }
            }
        })
    }

}