package com.threedev.appconvertor.data.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threedev.appconvertor.data.models.AllData
import com.threedev.appconvertor.data.models.UserFormInfo
import com.threedev.appconvertor.data.models.WebBuilderApkInfo
import com.threedev.appconvertor.data.repository.MainRepository
import com.threedev.appconvertor.ui.utils.Resource
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    private val _profileData = MutableLiveData<List<UserFormInfo>>()
    val profileData: LiveData<List<UserFormInfo>> = _profileData

    private val _appData = MutableLiveData<List<WebBuilderApkInfo>>()
    val appData: LiveData<List<WebBuilderApkInfo>> = _appData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchAllData() {
        viewModelScope.launch {
            try {
                repository.fetchData()
                /*val (profiles, apps) = repository.fetchAllData()
                _profileData.value = profiles
                _appData.value = apps*/
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching data: ${e.message}")
            }
        }
    }

    fun getAllData(): MutableLiveData<Resource<AllData>> {
        return repository.allData
    }

}
