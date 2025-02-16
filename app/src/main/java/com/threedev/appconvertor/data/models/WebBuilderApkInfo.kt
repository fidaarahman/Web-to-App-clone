package com.threedev.appconvertor.data.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class WebBuilderApkInfo(
    var docId: String,
    var appName: String,
    var packageName: String,
    var web_url: String,
    var image_url: String,
    var order_date: Long = Calendar.getInstance().timeInMillis,
    var delivery_date: Long = Calendar.getInstance().timeInMillis,
    var status: Int=0,
    var pullToRefresh: Boolean = false,
    var backCompatibility: Boolean = false,
    var appExit: String = "",
) : Parcelable{
    constructor() : this("", "", "", "", "", Calendar.getInstance().timeInMillis, Calendar.getInstance().timeInMillis,0)
}
