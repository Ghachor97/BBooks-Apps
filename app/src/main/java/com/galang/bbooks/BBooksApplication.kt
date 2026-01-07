package com.galang.bbooks

import android.app.Application
import com.galang.bbooks.data.AppDatabase
import com.galang.bbooks.data.repository.BookRepository
import com.galang.bbooks.data.repository.TransactionRepository
import com.galang.bbooks.data.repository.UserRepository

class BBooksApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val bookRepository by lazy { BookRepository(database.bookDao()) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
}
