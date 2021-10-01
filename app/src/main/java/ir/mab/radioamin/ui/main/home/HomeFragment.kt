package ir.mab.radioamin.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.databinding.FragmentHomeBinding
import ir.mab.radioamin.util.errorToast
import ir.mab.radioamin.util.networkErrorToast
import ir.mab.radioamin.vm.remote.MainViewModel
import ir.mab.radioamin.vo.enums.ErrorStatus
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment: Fragment() {
    lateinit var binding: FragmentHomeBinding

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

        getHomeTopics()

    }

    private fun getHomeTopics() {
        Timber.d("getHomeTopics")
        mainViewModel.getHomeTopics().observe(viewLifecycleOwner, {
            Timber.d("getHomeTopics %s", it.toString())
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{

                }
                Status.ERROR ->{
                    when(it.errorStatus){
                        ErrorStatus.NETWORK_CONNECTION_ERROR ->{
                            requireContext().networkErrorToast()
                        }
                        ErrorStatus.HTTP_EXCEPTION ->{
                            requireContext().errorToast(it.errorData.toString())
                        }
                        ErrorStatus.IO_EXCEPTION ->{
                            requireContext().errorToast(it.message.toString())
                        }
                    }
                }
            }
        })
    }
}