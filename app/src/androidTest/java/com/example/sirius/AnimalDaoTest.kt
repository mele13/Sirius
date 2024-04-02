package com.example.sirius

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sirius.data.SiriusDatabase
import com.example.sirius.data.dao.AnimalDao
import com.example.sirius.model.Animal
import com.example.sirius.model.TypeAnimal
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AnimalDaoTest {
    private lateinit var animalDao: AnimalDao
    private lateinit var siriusDatabase: SiriusDatabase

    //name, birt_name(yyyy-mm-dd), sex, waiting_adoption, foster_care, short_info, long_info, breed, type_animal, entry_date, photo_animal
    private var animal1 = Animal(
        1,
        "Thor",
        "2023-11-27",
        "M",
        0,
        0,
        "a",
        "a",
        "a",
        TypeAnimal.DOG,
        "2023-11-27",
        "res/drawable/goldenretriever1.jpg",
    )
    private var animal2 = Animal(
        2,
        "X",
        "2023-11-27",
        "M",
        0,
        0,
        "a",
        "a",
        "a",
        TypeAnimal.DOG,
        "2023-11-27",
        "res/drawable/goldenretriever1.jpg",
    )

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        siriusDatabase = Room.inMemoryDatabaseBuilder(context, SiriusDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        animalDao = siriusDatabase.animalDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        siriusDatabase.close()
    }

    private suspend fun addOneItemToDb() {
        animalDao.insertAnimal(animal1)
    }

    private suspend fun addTwoItemsToDb() {
        animalDao.insertAnimal(animal1)
        animalDao.insertAnimal(animal2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems = animalDao.getAllAnimals().first()
        assertEquals(allItems[0], animal1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = animalDao.getAllAnimals().first()
        assertEquals(allItems[0], animal1)
        assertEquals(allItems[1], animal2)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdate_updatesItemInDB() = runBlocking {
        addOneItemToDb()

        // Modify some properties of the animal
        val updatedAnimal = animal1.copy(shortInfoAnimal = "Updated Short Info")
        animalDao.updateAnimal(updatedAnimal)

        val allItems = animalDao.getAllAnimals().first()
        assertEquals(allItems[0], updatedAnimal)
    }

    @Test
    @Throws(Exception::class)
    fun daoDelete_deletesItemFromDB() = runBlocking {
        addOneItemToDb()

        // Delete the animal
        animalDao.deleteAnimal(animal1)

        val allItems = animalDao.getAllAnimals().first()
        assertTrue(allItems.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoQueryById_returnsCorrectItem() = runBlocking {
        addTwoItemsToDb()

        // Query an animal by ID
        val queriedAnimalFlow = animalDao.getAnimalById(2)

        // Collect the value from the flow
        val queriedAnimal = queriedAnimalFlow.first()

        // Assert that the contents are equal
        assertEquals(queriedAnimal, animal2)
    }

    @Test
    @Throws(Exception::class)
    fun daoQueryByName_returnsCorrectItems() = runBlocking {
        addTwoItemsToDb()

        // Query animals by name
        val breedAnimal1Flow = animalDao.getAnimalByName(animal1.nameAnimal)
        val breedAnimal2Flow = animalDao.getAnimalByName(animal2.nameAnimal)

        val breedAnimals1 = breedAnimal1Flow.first()[0]
        val breedAnimals2 = breedAnimal2Flow.first()[0]

        assertEquals(animal1, breedAnimals1)
        assertEquals(animal2, breedAnimals2)
    }
}