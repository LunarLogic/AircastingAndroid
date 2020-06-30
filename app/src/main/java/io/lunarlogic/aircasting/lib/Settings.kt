package io.lunarlogic.aircasting.lib

import android.app.Application
import android.content.SharedPreferences

open class Settings(mApplication: Application) {
    private val PRIVATE_MODE = 0
    private val PREFERENCES_NAME = "auth_token"
    private val AUTH_TOKEN_KEY = "auth_token"
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = mApplication.getSharedPreferences(PREFERENCES_NAME, PRIVATE_MODE)
    }

    open fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }

//    fun getEmail(): String? {
//        return sharedPreferences.getString(EMAIL_KEY, null)
//    }
//
//    fun setEmail(email: String) {
//        saveToSettings(EMAIL_KEY, email)
//    }
//
    fun setAuthToken(authToken: String) {
        saveToSettings(AUTH_TOKEN_KEY, authToken)
    }
//
    private fun saveToSettings(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()
    }
}
