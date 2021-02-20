package com.example.authorizationapp.viewmodel

import android.app.Application
import android.text.format.DateFormat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.authorizationapp.data.AuthNetworking
import com.example.authorizationapp.data.DemoServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class AuthorizationViewModel(application: Application) : AndroidViewModel(application) {

    private val authorizedKey = booleanPreferencesKey("authorizedKey")
    private val lastAuthorizationDateKey = stringPreferencesKey("lastAuthorizationDateKey")
    private val tokenKey = stringPreferencesKey("tokenKey")
    private val currentVersionKey = longPreferencesKey("currentVersionKey")
    private val dataStore: DataStore<Preferences> = application.createDataStore(name = "authorizationDataStore")

    val authorized
        get() = dataStore.data.map { preferences -> preferences[authorizedKey] ?: false }
    val lastAuthorizationDate
        get() = dataStore.data.map { preferences -> preferences[lastAuthorizationDateKey] }
    val token
        get() = dataStore.data.map { preferences -> preferences[tokenKey] }
    val currentVersion
        get() = dataStore.data.map { preferences -> preferences[currentVersionKey] }


    fun getActualAppVersion() = liveData(Dispatchers.IO) {
        token.collect {
            if (it != null) {
                val actualVersion = AuthNetworking.getActualAppVersion(it)
                dataStore.edit { it[currentVersionKey] = actualVersion }
                emit(actualVersion)
            }
        }
    }

    /**
     * use one of the following email/password (they have been set as correct credentials):
     *
     * adam@gmail.com, 12345678
     * yana.l@yahoo.com, abcdefg12
     * someone@gmail.com, AbCd12345
     */
    fun signIn(email: String, password: String) = liveData(Dispatchers.IO) {
        val token = AuthNetworking.signIn(email, password)
        val succeeded = (token != DemoServer.ERROR && token != DemoServer.ERROR_CREDENTIALS)
        if (succeeded) dataStore.edit {
            val date = Date()
            val dateFormat = DateFormat.getDateFormat(getApplication())
            it[lastAuthorizationDateKey] = dateFormat.format(date)
        }
        dataStore.edit { it[authorizedKey] = succeeded}
        dataStore.edit { it[tokenKey] = token }
        emit(token)
    }

    fun signOut() = viewModelScope.launch {
        dataStore.edit {
            it.remove(tokenKey)
            it[authorizedKey] = false
        }
    }
}