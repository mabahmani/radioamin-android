package ir.mab.radioamin.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.repo.DeviceAlbumRepository
import ir.mab.radioamin.vo.DeviceAlbum
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceAlbumsViewModel @Inject constructor(
    private val deviceAlbumRepository: DeviceAlbumRepository
) : ViewModel() {

    fun getDeviceAlbums(): LiveData<Resource<List<DeviceAlbum>>> {
        var result: LiveData<Resource<List<DeviceAlbum>>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceAlbumRepository.getDeviceAlbums()
        }

        return result
    }

    fun getDeviceAlbum(albumId: Long): LiveData<Resource<DeviceAlbum>> {
        var result: LiveData<Resource<DeviceAlbum>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceAlbumRepository.getDeviceAlbum(albumId)
        }

        return result
    }

    fun getDeviceAlbumSongs(albumId: Long): LiveData<Resource<List<DeviceSong>>> {
        var result: LiveData<Resource<List<DeviceSong>>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceAlbumRepository.getDeviceAlbumSongs(albumId)
        }

        return result
    }

}