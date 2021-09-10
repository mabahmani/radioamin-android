package ir.mab.radioamin.ui.deviceonly

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.FragmentFilterDeviceFoldersBinding
import ir.mab.radioamin.util.AppConstants
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.util.snack
import ir.mab.radioamin.vm.devicefiles.DeviceSongsViewModel
import ir.mab.radioamin.vo.devicefiles.DeviceSongFolder
import ir.mab.radioamin.vo.generic.Status
import javax.inject.Inject

@AndroidEntryPoint
class FilterDeviceFoldersFragment : Fragment() {
    private lateinit var binding: FragmentFilterDeviceFoldersBinding
    private val deviceSongsViewModel: DeviceSongsViewModel by viewModels()
    private val filterDeviceFoldersAdapter = FilterDeviceFoldersAdapter(mutableListOf())
    private var folders: List<DeviceSongFolder> = mutableListOf()
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterDeviceFoldersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getDeviceSongsFolders()
        setListeners()
    }


    private fun setListeners() {

        binding.refreshLayout.setOnRefreshListener {
            getDeviceSongsFolders()
        }

        binding.done.setOnClickListener {
            val stringSet: MutableSet<String> = mutableSetOf()
            for (folder in folders) {
                if (!folder.selected)
                    stringSet.add(folder.path)
            }

            sharedPreferences.apply {
                edit {
                    putStringSet(
                        AppConstants.Prefs.BLACK_LIST_FOLDERS,
                        stringSet
                    )
                }
            }
            requireActivity().snack(getString(R.string.blacklist_folders_updated))
            findNavController().popBackStack()
        }
    }

    private fun initList() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = filterDeviceFoldersAdapter
    }

    private fun getDeviceSongsFolders() {
        deviceSongsViewModel.getDeviceSongsFolders().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.showProgress = true
                }

                Status.SUCCESS -> {
                    folders = it.data ?: mutableListOf()
                    filterDeviceFoldersAdapter.list = folders
                    filterDeviceFoldersAdapter.notifyDataSetChanged()

                    binding.showProgress = false
                    binding.refreshLayout.isRefreshing = false

                }

                Status.ERROR -> {
                    requireContext().errorToast(it.message.toString())
                    binding.showProgress = false
                    binding.refreshLayout.isRefreshing = false

                }
            }
        })
    }

}