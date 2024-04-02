package com.example.sirius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.UserDao
import com.example.sirius.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.lang.Exception

class UserViewModel(private val userDao: UserDao) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun login(username: String, password: String): Boolean {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                try {
                    if (username.isBlank() || password.isBlank()) {
                        continuation.resume(false)
                        return@launch
                    }
                    val user = getUserByCredentials(username, password)
                    val success = user != null
                    if (user != null) {
                        _currentUser.value = user
                        saveAuthenticationState(user)
                    }
                    continuation.resume(success)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continuation.resume(false)
                }
            }
        }
    }

    fun getAuthenticatedUser(): User? {
        return _currentUser.value
    }

    private fun saveAuthenticationState(user: User?) {
        val sharedPreferences = AnimalApplication.context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            if (user != null)
                putString("user_info", Gson().toJson(user))
            else
                remove("user_info")
            apply()
        }
    }

    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        if (username.isBlank() || email.isBlank() || password.isBlank()) return false
        if (!checkIfUserExists(username)) {
            return try {
                val newUser = User(
                    username = username,
                    email = email,
                    password = password,
                    role = "user",
                    photoUser = "res/drawable/user_default_image.jpg",
                )
                viewModelScope.launch {
                    insertUser(newUser)
                    _currentUser.value = newUser
                    saveAuthenticationState(newUser)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        return false
    }

    suspend fun logout() {
        _currentUser.value = null
        saveAuthenticationState(null)
    }

    suspend fun checkIfUserExists(username: String): Boolean {
        if (getUserByUsername(username) != null) return true
        return false
    }

    suspend fun updateProfilePhoto(user: User, newPhoto: String) {
        user.photoUser = newPhoto
        userDao.updateProfilePhoto(user.id, newPhoto)
        _currentUser.value = user
    }

    suspend fun updateUserName(user: User, newUserName: String) {
        user.username = newUserName
        userDao.update(user)
    }

    suspend fun updateEmail(user: User, newEmail: String): Boolean {
        return if (isEmailAvailable(newEmail)) {
            user.email = newEmail
            userDao.update(user)
            true
        } else {
            false
        }
    }

    private suspend fun isEmailAvailable(email: String): Boolean {
        val existingUser = getUserByEmail(email)
        return existingUser == null
    }

    suspend fun updatePassword(user: User, currentPassword: String, newPassword: String): Boolean {
        return try {
            val existingUser = userDao.getUserByCredentials(user.username, newPassword)

            if (user.password == currentPassword && newPassword != currentPassword && existingUser == null) {
                user.password = newPassword
                userDao.update(user)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun getUserByCredentials(username: String, password: String): User? {
        return userDao.getUserByCredentials(username, password)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun getLikedAnimals(userId: Int) = userDao.getLikedAnimals(userId)

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                UserViewModel(application.database.userDao())
            }
        }
    }
}
