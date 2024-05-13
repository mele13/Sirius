package com.example.sirius.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sirius.data.dao.AnimalDao
import com.example.sirius.model.Animal
import com.example.sirius.model.LikedAnimal
import com.example.sirius.model.TypeAnimal
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AnimalViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var animalDao: AnimalDao

    private lateinit var viewModel: AnimalViewModel

    companion object {
        const val date = "2023-11-27"
        const val date1 = "2023-11-17"
        const val date2 = "2023-01-27"


    }
    private var animal1 = Animal(
        1,
        "Thor",
        date,
        "M",
        0,
        0,
        "a",
        "a",
        "a",
        TypeAnimal.DOG,
        date,
        "res/drawable/goldenretriever1.jpg",
        0,1,5
    )
    private var animal2 = Animal(
        2,
        "X",
        date,
        "M",
        0,
        0,
        "a",
        "a",
        "a",
        TypeAnimal.DOG,
        date,
        "res/drawable/goldenretriever1.jpg",
        0,1,2
    )
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AnimalViewModel(animalDao)
    }


    @Test
    fun getAllAnimals() = runBlocking {
        val mockList = listOf(animal1   , animal2)
        `when`(animalDao.getAllAnimals()).thenReturn(flow { emit(mockList) })

        viewModel.getAllAnimals().collect { animals ->
            assert(animals.size == 2)
        }
    }
    @Test
    fun insertAnimal() = runBlocking {
        // Arrange
        val animal = Animal(
            0,
            "Thor",
            date,
            "M",
            0,
            0,
            "a",
            "a",
            "a",
            TypeAnimal.DOG,
            date,
            "res/drawable/goldenretriever1.jpg",
            0,1,3
        )
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        coEvery { daoMock.insertAnimal(any()) } just runs

        // Act
        viewModel.insertAnimal(animal)

        // Assert
        coVerify(exactly = 1) { daoMock.insertAnimal(animal) }
    }

    @Test
    fun getAllAnimalsOrderedByDaysEntryDate() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val expectedList = listOf(
            Animal(15, "Blanquita", "2005-02-03", "F", 1, 1, "Melody Whisk", "Meet Blanquita, the charming 200gr feline songstress who serenades the world with her melodic meows at the delightful hour of 4 AM. This adorable senior lady brings a symphony of joy to your mornings, ensuring you wake up with a smile on your face.", "Mixed", TypeAnimal.CAT, "208-06-02", "res/drawable/blanquita1.jpg, res/drawable/blanquita2.jpg, res/drawable/blanquita3.jpg", 0, 0,1)
        )
        // Configure mock for getAllAnimalsOrderedByDaysEntryDate()
        coEvery { daoMock.getAllAnimalsOrderedByDaysEntryDate() } returns flowOf(expectedList)

        // Setup mock for getAllAnimals()
        coEvery { daoMock.getAllAnimals() } returns flowOf(expectedList) // ou listOf(animalFicticio)

        // Act
        val animals = viewModel.getAllAnimalsOrderedByDaysEntryDate().toList()

        // Assert
        assertEquals(expectedList, animals[0])
    }


    @Test
    fun getBirthYears() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val expectedList = listOf("2023", "2022", "2021") // Ejemplo de lista de años de nacimiento esperados
        coEvery { daoMock.getBirthYears() } returns flow { emit(expectedList) }

        // Act
        val birthYears = viewModel.getBirthYears().toList()

        // Assert
        assertEquals(expectedList, birthYears[0])
    }

    @Test
    fun getBreed() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val expectedBreed = listOf("Common European", "Mixed", "Yorkshire", "Majorero dog", "Agapornis", "German Shepperd")
        coEvery { daoMock.getBreed() } returns flow { emit(expectedBreed) }

        // Act
        val breed = viewModel.getBreed().toList()

        // Assert
        assertEquals(expectedBreed, breed[0])
    }


    @Test
    fun getTypeAnimal() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val expectedType = listOf(TypeAnimal.CAT.toString(), TypeAnimal.DOG.toString(), TypeAnimal.BIRD.toString())
        coEvery { daoMock.getTypeAnimal() }  returns flow { emit(expectedType) }

        // Act
        val type = viewModel.getTypeAnimal().toList()

        // Assert
        assertEquals(expectedType, type[0])
    }

    @Test
    fun getAnimalsByAgeDesc() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val expectedList = listOf(
            Animal(1, "Thor", "2023-11-27", "M", 0, 0, "a", "a", "a", TypeAnimal.DOG, "2023-11-27", "res/drawable/goldenretriever1.jpg", 0, 1,2),
            Animal(2, "X", "2023-11-27", "M", 0, 0, "a", "a", "a", TypeAnimal.DOG, "2023-11-27", "res/drawable/goldenretriever1.jpg", 0, 1,2)
        )
        // Configure mock for getAnimalsByAgeDesc()
        coEvery { daoMock.getAnimalsByAgeDesc("2023-11-27") } returns flowOf(expectedList)

        // Act
        val animals = viewModel.getAnimalsByAgeDesc("2023-11-27").toList()

        // Assert
        assertEquals(expectedList, animals[0])
    }

    @Test
    fun getAnimalsByBreed() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val breed = "Labrador" // Breed que se está probando
        val expectedList = listOf(
            Animal(1, "Max", "2023-01-27", "M", 0, 0, "a", "a", "a", TypeAnimal.DOG, "2023-01-27", "res/drawable/labrador.jpg", 0, 1,2),
            Animal(2, "Bella", "2023-01-27", "F", 0, 0, "a", "a", "a", TypeAnimal.DOG, "2023-01-27", "res/drawable/labrador.jpg", 0, 1,2)
        )
        // Configure mock for getAnimalsByBreed()
        coEvery { daoMock.getAnimalsByBreed(breed) } returns flowOf(expectedList)

        // Act
        val animals = viewModel.getAnimalsByBreed(breed).toList()

        // Assert
        assertEquals(expectedList, animals[0])
    }

    @Test
    fun getAnimalsByTypeAnimal() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val typeAnimal = TypeAnimal.DOG.toString() // Tipo de animal que se está probando
        val expectedList = listOf(
            Animal(1, "Max", "2023-01-27", "M", 0, 0, "a", "a", "a", TypeAnimal.DOG, "2023-01-27", "res/drawable/labrador.jpg", 0, 1,2),
            Animal(2, "Bella", "2023-01-27", "F", 0, 0, "a", "a", "a", TypeAnimal.DOG, "2023-01-27", "res/drawable/labrador.jpg", 0, 1,2)
        )
        // Configure mock for getAnimalsByTypeAnimal()
        coEvery { daoMock.getAnimalsByTypeAnimal(typeAnimal) } returns flowOf(expectedList)

        // Act
        val animals = viewModel.getAnimalsByTypeAnimal(typeAnimal).toList()

        // Assert
        assertEquals(expectedList, animals[0])
    }

    @Test
    fun getAnimalById() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val animalId = 1
        val expectedAnimal = Animal(
            1,
            "Max",
            "2023-01-27",
            "M",
            0,
            0,
            "a",
            "a",
            "a",
            TypeAnimal.DOG,
            "2023-01-27",
            "res/drawable/labrador.jpg",
            0,
            1,2
        )
        // Configure mock for getAnimalById()
        coEvery { daoMock.getAnimalById(animalId) } returns flowOf(expectedAnimal)

        // Act
        val animal = viewModel.getAnimalById(animalId).toList()

        // Assert
        assertEquals(expectedAnimal, animal[0])
    }


    @Test
    fun insertLikedAnimal() = runBlocking {
        // Arrange
        val daoMock = mockk<AnimalDao>()
        val viewModel = AnimalViewModel(daoMock)
        val likedAnimal = LikedAnimal(1, 1) // Creamos un nuevo LikedAnimal con un usuario y animal específicos
        // Configura la respuesta mockk para la función insertLikedAnimal
        coEvery { daoMock.insertLikedAnimal(likedAnimal) } just runs

        // Act
        // Llama a la función insertLikedAnimal del ViewModel con los parámetros correctos
        viewModel.insertLikedAnimal(likedAnimal.userId, likedAnimal.animalId)

        // Assert
        // Verifica que la función insertLikedAnimal del DAO fue llamada exactamente una vez con los parámetros correctos
        coVerify(exactly = 1) { daoMock.insertLikedAnimal(likedAnimal) }
    }

    @Test
    fun removeLikedAnimal() {
    }

    @Test
    fun getLikedAnimals() {
    }

    @Test
    fun updateAnimal() {
    }

    @Test
    fun deleteAnimal() {
    }

    @Test
    fun getOurFriends() {
    }

    @Test
    fun getLostAnimals() {
    }
}