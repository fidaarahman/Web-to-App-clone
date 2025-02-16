package com.threedev.appconvertor
import com.threedev.appconvertor.data.models.WebBuilderApkInfo

interface AppClickListener {
    fun onInstructionClick(app: WebBuilderApkInfo)
    fun onGetStartedClick(app: WebBuilderApkInfo)
}
