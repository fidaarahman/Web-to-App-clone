package com.threedev.appconvertor.helpers

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder


object SessionManager {

    private val PREF_NAME: String = "app.pref"
    lateinit var preferences: SharedPreferences

    fun with(application: Application) {
        preferences = application.getSharedPreferences(PREF_NAME , Context.MODE_PRIVATE)
    }

    fun <T> setObject(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        preferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> getObject(key: String): T? {
        val value = preferences.getString(key, null)
        val type = object : TypeToken<T>() {}.type
        return GsonBuilder().create().fromJson(value, type)
    }

    fun setString(key: String, value:String){
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defVal: String):String{
        return preferences.getString(key, defVal).toString()
    }

    fun putBool(key: String, value:Boolean){
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getBool(key: String, defVal:Boolean):Boolean{
        return preferences.getBoolean(key, defVal)
    }

    fun setInt(key: String, value:Int){
        preferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defVal: Int=0):Int{
        return preferences.getInt(key, defVal)
    }

    const val APP_LIMIT_KEY: String = "App.convert_launch"
    const val IS_REMOVE_AD_PURCHASED = "printer.purchase.ads"
    const val KEY_LOCALE_LANGUAGE = "printer.locale.language"

}