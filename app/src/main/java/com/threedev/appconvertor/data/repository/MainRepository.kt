package com.threedev.appconvertor.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.threedev.appconvertor.data.models.AllData
import com.threedev.appconvertor.data.models.UserFormInfo
import com.threedev.appconvertor.data.models.WebBuilderApkInfo
import com.threedev.appconvertor.ui.utils.Resource
import kotlinx.coroutines.tasks.await

class MainRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    suspend fun fetchAllData(): Pair<List<UserFormInfo>, List<WebBuilderApkInfo>> {
        val profileList = mutableListOf<UserFormInfo>()
        val appList = mutableListOf<WebBuilderApkInfo>()

        currentUser?.let { user ->
            // Single Firestore query
            val result = firestore.collection(user.uid).get().await()

            for (document in result) {

                if (document.contains("name") && document.contains("phone") && document.contains("dob")) {
                    val profile = document.toObject(UserFormInfo::class.java)
                    profileList.add(profile)
                } else if (document.contains("appName") && document.contains("packageName")
                    && document.contains("web_url") && document.contains("image_url")
                ) {
                    val app = document.toObject(WebBuilderApkInfo::class.java)
                    appList.add(app)
                }
            }
        } ?: throw Exception("User not logged in")


        return Pair(profileList, appList)
    }

    val allData:MutableLiveData<Resource<AllData>> = MutableLiveData()
    suspend fun fetchData(){
        allData.postValue(Resource.loading(500, null))
        val allDataModel = AllData(UserFormInfo(), ArrayList())
        if (currentUser != null){
            val result = firestore.collection(currentUser.uid).get().await()
            for (document in result) {
                Log.d("Firestore", "Document: $document")
                if (document.contains("name") && document.contains("phone") && document.contains("dob")) {
                    val profile = document.toObject(UserFormInfo::class.java)
                    allDataModel.profile = profile
                } else if (document.contains("appName") && document.contains("packageName")
                    && document.contains("web_url") && document.contains("image_url")
                ) {
                    val app = document.toObject(WebBuilderApkInfo::class.java)
                    allDataModel.builderData.add(app)
                }
            }
            allData.postValue(Resource.success(200, allDataModel))
        }else{
            allData.postValue(Resource.error(402, "Unauthorized access", null))
        }
    }

}


