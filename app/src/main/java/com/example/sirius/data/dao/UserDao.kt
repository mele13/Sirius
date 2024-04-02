package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sirius.model.Animal
import com.example.sirius.model.User
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM User WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM User WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    suspend fun getUserByCredentials(username: String, password: String): User?

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM User")
    suspend fun deleteAllUsers()

    @Query("SELECT animal.* FROM Animal INNER JOIN LikedAnimal ON animal.id = LikedAnimal.animal_id WHERE LikedAnimal.user_id = :userId")
    fun getLikedAnimals(userId: Int): Flow<List<Animal>>

    @Query("UPDATE User SET photo_user = :newPhoto WHERE id = :userId")
    suspend fun updateProfilePhoto(userId: Int, newPhoto: String)

}
