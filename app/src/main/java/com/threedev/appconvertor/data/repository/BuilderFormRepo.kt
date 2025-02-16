package com.threedev.appconvertor.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.threedev.appconvertor.ui.utils.Resource
import java.util.Calendar

class BuilderFormRepo {

    val uploadFile:MutableLiveData<Resource<String>> = MutableLiveData()

    suspend fun uploadImage(imageUri: Uri) {
        val appIcon = "icon-${Calendar.getInstance().timeInMillis}.jpg"
        val mountainImagesRef = Firebase.storage.reference.child("app_icon/$appIcon")
        val uploadTask = mountainImagesRef.putFile(imageUri)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            uploadFile.postValue(Resource.error(500, "Failed to upload", null))
        }.addOnSuccessListener { taskSnapshot ->
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                mountainImagesRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    uploadFile.postValue(Resource.success(200, downloadUri.toString()))
                } else {
                   uploadFile.postValue(Resource.error(400, "Failed to upload", null))
                }
            }.addOnFailureListener {
                uploadFile.postValue(Resource.error(400, "Failed to upload", null))
            }
        }

    }


}