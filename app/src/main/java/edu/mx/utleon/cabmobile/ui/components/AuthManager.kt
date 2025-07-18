package edu.mx.utleon.cabmobile.ui.components

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager (context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "user_id"
        private const val USER_EMAIL_KEY = "user_email"
    }

    init {
        _isLoggedIn.value = getToken() != null
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
        _isLoggedIn.value = true
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getBearerToken(): String? {
        val token = getToken()
        return if (token != null) "Bearer $token" else null
    }

    fun saveUserData(userId: String, email: String) {
        sharedPreferences.edit()
            .putString(USER_ID_KEY, userId)
            .putString(USER_EMAIL_KEY, email)
            .apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(USER_EMAIL_KEY, null)
    }

    fun logout() {
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .remove(USER_ID_KEY)
            .remove(USER_EMAIL_KEY)
            .apply()
        _isLoggedIn.value = false
    }

    fun isAuthenticated(): Boolean {
        return getToken() != null
    }
}