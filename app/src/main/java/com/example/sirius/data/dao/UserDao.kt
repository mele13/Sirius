package com.example.sirius.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sirius.model.Animal
import com.example.sirius.model.User
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM user WHERE (role = 'worker' OR role = 'volunteer') AND shelter_id = :shelterId ORDER BY username ASC")
    suspend fun getAllEmployers(shelterId: Int): List<User>

    @Query("DELETE FROM User")
    fun deleteAllUsers()

    @Query("SELECT animal.* FROM Animal INNER JOIN LikedAnimal ON animal.id = LikedAnimal.animal_id WHERE LikedAnimal.user_id = :userId")
    fun getLikedAnimals(userId: Int): Flow<List<Animal>>

    @Query("UPDATE User SET photo_user = :newPhoto WHERE id = :userId")
    suspend fun updateProfilePhoto(userId: Int, newPhoto: String)

    @Query("UPDATE User SET role = :newRole WHERE id = :userId")
    suspend fun updateRole(userId: Int, newRole: String)

    @Query("SELECT * FROM User WHERE role = 'worker' OR role = 'owner' ORDER BY RANDOM() LIMIT 1")
    fun getRandomWorkerOrOwner() : Flow<User?>
    @Query("SELECT * FROM User WHERE id != :id")
    suspend fun getAllUsersExceptAuthenticated(id : Int): List<User>

    @Query("SELECT shelter_id FROM User WHERE id = :id")
    fun getShelterByUserId(id : Int) : Flow<List<Int>>

    @Query("SELECT * FROM User WHERE shelter_id = :shelterId AND id != :id")
    suspend fun getUserFormMyShelters(shelterId : Int, id : Int): List<User>

    @Query("SELECT * FROM User WHERE  role = 'user'")
    suspend fun getUserWithRoleUser(): List<User>

    @Query("SELECT * FROM User WHERE  role = 'owner'")
    suspend fun getOwners(): List<User>
    @Query("SELECT * FROM User WHERE  role = 'admin'")
    suspend fun getAdmin(): List<User>

}
