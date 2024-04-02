package com.example.sirius.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sirius.data.dao.AnimalDao
import com.example.sirius.data.dao.NewsDao
import com.example.sirius.data.dao.UserDao
import com.example.sirius.model.Animal
import com.example.sirius.model.LikedAnimal
import com.example.sirius.model.News
import com.example.sirius.model.User

@Database(
    entities = [Animal::class, News::class, User::class, LikedAnimal::class],
    version = 1,
    exportSchema = false
)
abstract class SiriusDatabase: RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun newsDao(): NewsDao
    abstract fun userDao(): UserDao

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
//                    .createFromAsset(database_path)
//                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
