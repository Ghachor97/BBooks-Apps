package com.galang.bbooks

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.galang.bbooks.data.AppDatabase
import com.galang.bbooks.data.UserPreferences
import com.galang.bbooks.data.repository.BookRepository
import com.galang.bbooks.data.repository.TransactionRepository
import com.galang.bbooks.data.repository.UserRepository

class BBooksApplication : Application() {
    lateinit var container: AppContainer

    // Manual DI Container
    class AppContainer(context: Context) {
        private val db = Room.databaseBuilder(context, AppDatabase::class.java, "bbooks.db")
            .fallbackToDestructiveMigration()
            .build()
        
        val userPreferences = UserPreferences(context)
        
        val userRepository = UserRepository(db.userDao(), userPreferences)
        val bookRepository = BookRepository(db.bookDao())
        val transactionRepository = TransactionRepository(db.transactionDao())
    }

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    // Expose repositories for safe access (or just access container directly)
    val userRepository: UserRepository
        get() = container.userRepository
    val bookRepository: BookRepository
        get() = container.bookRepository
    val transactionRepository: TransactionRepository
        get() = container.transactionRepository
}
