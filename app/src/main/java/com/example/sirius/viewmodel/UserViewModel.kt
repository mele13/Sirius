package com.example.sirius.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.UserDao
import com.example.sirius.model.TypeUser
import com.example.sirius.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserViewModel(private val userDao: UserDao) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun login(username: String, password: String): String {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                try {
                    if (isInvalidCredentials(username, password)) {
                        continuation.resume("false")
                        return@launch
                    }

                    val user = getUserByCredentials(username, password)
                    if (user != null) {
                        handleSuccessfulLogin(user, continuation)
                    } else {
                        continuation.resume("false")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    continuation.resume("false")
                }
            }
        }
    }

    private fun isInvalidCredentials(username: String, password: String): Boolean {
        return username.isBlank() || password.isBlank()
    }

    private suspend fun handleSuccessfulLogin(user: User, continuation: Continuation<String>) {
        _currentUser.value = user
        saveAuthenticationState(user)
        val loginResult = if (user.role == TypeUser.owner) "shelter" else "true"
        continuation.resume(loginResult)
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

    suspend fun registerUser(username: String, email: String, password: String, role: TypeUser): Boolean {
        if (username.isBlank() || email.isBlank() || password.isBlank()) return false
        if (!checkIfUserExists(username)) {
            return try {
                val newUser = User(
                    username = username,
                    email = email,
                    password = password,
                    role = role,
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

    fun logout() {
        _currentUser.value = null
        saveAuthenticationState(null)
    }

    private suspend fun checkIfUserExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }

    suspend fun getAllEmployers(): List<User> {
        return userDao.getAllEmployers()
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

    private suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    private suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    private suspend fun getUserByCredentials(username: String, password: String): User? {
        return userDao.getUserByCredentials(username, password)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun getAllUsersExceptAuthenticated(id:Int): List<User> {
        return userDao.getAllUsersExceptAuthenticated(id)
    }

    private suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun getLikedAnimals(userId: Int) = userDao.getLikedAnimals(userId)


    suspend fun updateRole(user: User, newRole: TypeUser) {
        user.role = newRole
        userDao.update(user)
    }



    fun getShelterByUserId(id : Int) : Flow<List<Int>> = userDao.getShelterByUserId(id)
    fun getRandomUser() : Flow<User?> = userDao.getRandomWorkerOrOwner()
    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                UserViewModel(application.database.userDao())
            }
        }
    }

}
