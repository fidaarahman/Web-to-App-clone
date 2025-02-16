package com.threedev.appconvertor.ui.fragments.convertapp

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threedev.appconvertor.data.repository.BuilderFormRepo
import com.threedev.appconvertor.ui.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConvertViewModel : ViewModel() {
    val repo = BuilderFormRepo()

    fun uploadImage(imageUri:Uri){
        CoroutineScope(Dispatchers.IO).launch {
            repo.uploadImage(imageUri)
        }
    }

    fun onUploadImage(): MutableLiveData<Resource<String>> {
        return repo.uploadFile
    }
}