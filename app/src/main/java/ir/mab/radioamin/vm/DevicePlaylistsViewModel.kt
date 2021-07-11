package ir.mab.radioamin.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.repo.DevicePlaylistRepository
import ir.mab.radioamin.vo.DevicePlaylist
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicePlaylistsViewModel @Inject constructor(
    private val devicePlaylistRepository: DevicePlaylistRepository
): ViewModel() {

    fun getDevicePlaylists(): LiveData<Resource<List<DevicePlaylist>>>{
        var result: LiveData<Resource<List<DevicePlaylist>>> = MutableLiveData()
        viewModelScope.launch {
            result = devicePlaylistRepository.getDevicePlaylists()
        }

        return result
    }

    fun getDevicePlaylistMembers(playlistId: Long): LiveData<Resource<List<DeviceSong>>>{
        var result: LiveData<Resource<List<DeviceSong>>> = MutableLiveData()
        viewModelScope.launch {
            result = devicePlaylistRepository.getPlaylistMembers(playlistId)
        }

        return result
    }

    fun createPlaylist(title: String): LiveData<Resource<Uri>>{
        var result: LiveData<Resource<Uri>> = MutableLiveData()
        viewModelScope.launch {
            result = devicePlaylistRepository.addNewPlaylist(title)
        }
        return result
    }
}