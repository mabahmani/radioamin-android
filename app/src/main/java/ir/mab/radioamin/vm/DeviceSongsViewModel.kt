package ir.mab.radioamin.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mab.radioamin.repo.DeviceSongRepository
import ir.mab.radioamin.vo.DeviceSong
import ir.mab.radioamin.vo.DeviceSongTag
import ir.mab.radioamin.vo.generic.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceSongsViewModel @Inject constructor(
    private val deviceSongRepository: DeviceSongRepository
): ViewModel() {

    fun getDeviceSongs(): LiveData<Resource<List<DeviceSong>>>{
        var result: LiveData<Resource<List<DeviceSong>>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceSongRepository.getDeviceSongs()
        }

        return result
    }

    fun getDeviceSong(songId: Long): LiveData<Resource<DeviceSong>>{
        var result: LiveData<Resource<DeviceSong>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceSongRepository.getDeviceSong(songId)
        }

        return result
    }

    fun getDeviceSongTags(path: String): LiveData<Resource<DeviceSongTag>>{
        var result: LiveData<Resource<DeviceSongTag>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceSongRepository.getDeviceSongTags(path)
        }

        return result
    }

    fun writeDeviceSongTags(path: String, deviceSongTag: DeviceSongTag, coverArtUri: Uri?): LiveData<Resource<Boolean>>{
        var result: LiveData<Resource<Boolean>> = MutableLiveData()
        viewModelScope.launch {
            result = deviceSongRepository.writeDeviceSongTags(path, deviceSongTag, coverArtUri)
        }

        return result
    }
}