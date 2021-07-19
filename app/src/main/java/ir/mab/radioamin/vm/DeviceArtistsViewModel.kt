package ir.mab.radioamin.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.repo.DeviceArtistRepository
import ir.mab.radioamin.vo.DeviceArtist
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceArtistsViewModel @Inject constructor(
    private val deviceArtistRepository: DeviceArtistRepository
): ViewModel() {

    fun getDeviceArtists(): LiveData<Resource<List<DeviceArtist>>>{
        var result: LiveData<Resource<List<DeviceArtist>>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceArtistRepository.getDeviceArtists()
        }

        return result
    }

    fun getDeviceArtistSongs(artistId: Long): LiveData<Resource<List<DeviceSong>>>{
        var result: LiveData<Resource<List<DeviceSong>>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceArtistRepository.getDeviceArtistSongs(artistId)
        }

        return result
    }
}