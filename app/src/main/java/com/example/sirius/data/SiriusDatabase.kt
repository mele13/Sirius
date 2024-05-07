package com.example.sirius.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sirius.data.dao.AnimalDao
import com.example.sirius.data.dao.ChatDao
import com.example.sirius.data.dao.ClinicalRecordDao
import com.example.sirius.data.dao.EventDAO
import com.example.sirius.data.dao.ManagementDao
import com.example.sirius.data.dao.NewsDao
import com.example.sirius.data.dao.ShelterDao
import com.example.sirius.data.dao.UserDao
import com.example.sirius.model.Animal
import com.example.sirius.model.Chat
import com.example.sirius.model.ClinicalRecord
import com.example.sirius.model.Event
import com.example.sirius.model.LikedAnimal
import com.example.sirius.model.Management
import com.example.sirius.model.News
import com.example.sirius.model.Shelter
import com.example.sirius.model.User

@Database(
    entities = [Animal::class, News::class, User::class, LikedAnimal::class, Chat::class, Shelter::class, ClinicalRecord::class, Event::class, Management::class],
    version = 2,
    exportSchema = false
)
abstract class SiriusDatabase: RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun newsDao(): NewsDao
    abstract fun userDao(): UserDao

    abstract fun chatDao(): ChatDao
    abstract fun shelterDao(): ShelterDao
    abstract fun clinicalRecordDao(): ClinicalRecordDao
    abstract fun eventDao(): EventDAO
    abstract fun managementDao(): ManagementDao

    companion object {
        @Volatile
        private var INSTANCE: SiriusDatabase? = null
        private const val database_path: String = "database/Sirius.db"

        fun getDatabase(context: Context): SiriusDatabase {
            val temInstance = INSTANCE
            if(temInstance != null){
                return temInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiriusDatabase::class.java,
                    "app_database"
                )
                    .createFromAsset(database_path)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
