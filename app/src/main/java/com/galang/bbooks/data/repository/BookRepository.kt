package com.galang.bbooks.data.repository

import com.galang.bbooks.data.Book
import com.galang.bbooks.data.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class BookRepository(private val bookDao: BookDao) {
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    fun searchBooks(query: String): Flow<List<Book>> = bookDao.searchBooks(query)

    suspend fun getBook(id: Long): Book? = bookDao.getBookById(id)
    
    suspend fun addBook(book: Book) = bookDao.insertBook(book)
    
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)
    
    suspend fun ensureBooksExist() {
        val books = allBooks.firstOrNull()
        if (books.isNullOrEmpty()) {
            val defaults = listOf(
                Book(title = "Laskar Pelangi", author = "Andrea Hirata", category = "Novel", stock = 5, coverUrl = "", isAvailable = true),
                Book(title = "Bumi Manusia", author = "Pramoedya Ananta Toer", category = "Sastra", stock = 3, coverUrl = "", isAvailable = true),
                Book(title = "Clean Code", author = "Robert C. Martin", category = "Teknologi", stock = 2, coverUrl = "", isAvailable = true),
                Book(title = "Atomic Habits", author = "James Clear", category = "Self Help", stock = 10, coverUrl = "", isAvailable = true),
                Book(title = "Filosofi Teras", author = "Henry Manampiring", category = "Filosofi", stock = 7, coverUrl = "", isAvailable = true)
            )
            defaults.forEach { bookDao.insertBook(it) }
        }
    }
}
