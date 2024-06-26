package com.example.sirius.viewmodel

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sirius.AnimalApplication
import com.example.sirius.data.dao.ManagementDao
import com.example.sirius.model.Management
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import java.time.YearMonth

class ManagementViewModel(private val managementDao: ManagementDao) : ViewModel() {

    private val exportCompleted = MutableLiveData<Boolean>()

    fun getLastMovements(id : Int) : Flow<List<Management?>> = managementDao.getLastMovements(id)

    fun getMovements(id : Int) : Flow<List<Management?>> = managementDao.getMovements(id)


    fun calculateTotal(movements : List<Management?>?) : Double {
        var total = 0.0

        if (movements != null) {
            for (movement in movements) {
                val matchResult = movement?.let { Regex("""(-?\d+)""").find(it.value) }
                matchResult?.let {
                    val value = it.groupValues[1].toDouble()
                    total += value
                }
            }
        }

        return total
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateBalance(movements: List<Management?>?): Double {
        val currentMonth = YearMonth.now()
        var balance = 0.0

        movements?.forEach { movement ->
            movement?.let {
                val movementDate = LocalDate.ofEpochDay(movement.date.toLong() / (1000 * 3600 * 24))
                val movementYearMonth = YearMonth.of(movementDate.year, movementDate.month)
                if (movementYearMonth == currentMonth) {
                    val value = movement.value.toDoubleOrNull() ?: 0.0
                    balance += value
                }
            }
        }

        return balance
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getMovementsForCurrentMonth(movements: List<Management?>?): List<Management> {
        val currentMonth = YearMonth.now()
        val currentMonthMovements = mutableListOf<Management>()

        movements?.forEach { movement ->
            movement?.let {
                val movementDate = LocalDate.ofEpochDay(movement.date.toLong() / (1000 * 3600 * 24))
                val movementYearMonth = YearMonth.of(movementDate.year, movementDate.month)
                if (movementYearMonth == currentMonth) {
                    currentMonthMovements.add(movement)
                }
            }
        }
        return currentMonthMovements.toList()
    }
    //
    //
    suspend fun exportTableAText(context: Context, outputFile: String) {
        withContext(Dispatchers.IO) {
            val items = managementDao.getManagement()

            val file = File(context.getExternalFilesDir(null), outputFile)
            val writer = FileWriter(file)

            items.forEach { item ->
                writer.append(item.toString())
                writer.append('\n')
            }

            writer.flush()
            writer.close()

            val sourceFile = File(context.getExternalFilesDir(null), outputFile)
            val destinationFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), outputFile)
            sourceFile.copyTo(destinationFile, true)

            exportCompleted.postValue(true)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalApplication)
                ManagementViewModel(application.database.managementDao())
            }
        }
    }

}