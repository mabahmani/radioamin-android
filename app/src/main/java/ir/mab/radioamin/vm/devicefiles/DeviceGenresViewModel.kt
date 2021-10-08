package ir.mab.radioamin.vm.devicefiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.repo.DeviceGenreRepository
import ir.mab.radioamin.vo.devicefiles.DeviceGenre
import ir.mab.radioamin.vo.devicefiles.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceGenresViewModel @Inject constructor(
    private val deviceGenreRepository: DeviceGenreRepository
): ViewModel() {

    fun getDeviceGenres(): LiveData<Resource<List<DeviceGenre>>>{
        var result: LiveData<Resource<List<DeviceGenre>>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceGenreRepository.getDeviceGenres()
        }

        return result
    }

    fun getDeviceGenre(genreID: Long): LiveData<Resource<DeviceGenre>>{
        var result: LiveData<Resource<DeviceGenre>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceGenreRepository.getDeviceGenre(genreID)
        }

        return result
    }

    fun getDeviceGenreMembers(genreId: Long): LiveData<Resource<List<DeviceSong>>>{
        var result: LiveData<Resource<List<DeviceSong>>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceGenreRepository.getGenreMembers(genreId)
        }

        return result
    }

}