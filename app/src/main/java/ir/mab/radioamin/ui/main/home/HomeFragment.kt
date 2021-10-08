package ir.mab.radioamin.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentHomeBinding
import ir.mab.radioamin.vm.remote.MainViewModel
import ir.mab.radioamin.vo.generic.Status
import ir.mab.radioamin.vo.res.Topic
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment: Fragment() {
    lateinit var binding: FragmentHomeBinding

    //private val mainViewModel: MainViewModel by navGraphViewModels(R.id.main_nav_graph){defaultViewModelProviderFactory}
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("mainViewModel %s", mainViewModel)
        observeRefreshLayout()
        getHomeTopics()
    }

    private fun observeRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            getHomeTopics(true)
        }
    }

    private fun getHomeTopics(forceRefresh: Boolean = false) {
        Timber.d("getHomeTopics")
        mainViewModel.getHomeTopics(forceRefresh).observe(viewLifecycleOwner, {
            Timber.d("getHomeTopics %s", it.toString())
            when(it.status){
                Status.LOADING ->{
                    if(it.data == null)
                        binding.showProgress = true
                }
                Status.SUCCESS ->{
                    binding.refreshLayout.isRefreshing = false
                    binding.showProgress = false
                    setupList(it.data?.data?.topics)
                }
                Status.ERROR ->{
                    binding.refreshLayout.isRefreshing = false
                    binding.showProgress = false
//                    when(it.errorStatus){
//                        ErrorStatus.NETWORK_CONNECTION_ERROR ->{
//                            requireContext().networkErrorToast()
//                        }
//                        ErrorStatus.HTTP_EXCEPTION ->{
//                            requireContext().errorToast(it.errorData.toString())
//                        }
//                        ErrorStatus.IO_EXCEPTION ->{
//                            requireContext().errorToast(it.message.toString())
//                        }
//                    }
                }
            }
        })
    }

    private fun setupList(topics: List<Topic>?) {
        if (!topics.isNullOrEmpty()){
            binding.list.layoutManager = LinearLayoutManager(requireContext())
            binding.list.adapter = HomeTopicAdapter(topics)
        }
    }
}