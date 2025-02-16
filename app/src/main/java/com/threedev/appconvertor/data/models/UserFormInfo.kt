package com.threedev.appconvertor.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class UserFormInfo(
    val name:String,
    val phone:String,
    val country: String
) : Parcelable {
    constructor() : this("","","")
}

