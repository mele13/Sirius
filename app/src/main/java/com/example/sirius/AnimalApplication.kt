package com.example.sirius

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.sirius.data.SiriusDatabase

class AnimalApplication: Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    val database: SiriusDatabase by lazy { SiriusDatabase.getDatabase(this) }
    val userDao by lazy { database.userDao() }
    val chatDao by lazy { database.chatDao() }
    val eventDao by lazy {database.eventDao()}

    fun initContext(appContext: Context) {
        context = appContext
    }
}