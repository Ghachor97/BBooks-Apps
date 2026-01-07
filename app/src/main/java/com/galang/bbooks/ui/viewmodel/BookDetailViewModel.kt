package com.galang.bbooks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galang.bbooks.data.Book
import com.galang.bbooks.data.Transaction
import com.galang.bbooks.data.repository.BookRepository
import com.galang.bbooks.data.repository.TransactionRepository
import com.galang.bbooks.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val bookId: Long
) : ViewModel() {

    private val _book = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    private val _borrowState = MutableStateFlow<BorrowState>(BorrowState.Idle)
    val borrowState = _borrowState.asStateFlow()

    init {
        viewModelScope.launch {
            _book.value = bookRepository.getBook(bookId)
        }
    }

    fun borrowBook() {
        val currentBook = _book.value ?: return
        val user = userRepository.currentUser
        
        if (user == null) {
            _borrowState.value = BorrowState.Error("Anda harus login kembali")
            return
        }
        
        if (currentBook.stock <= 0 || !currentBook.isAvailable) {
             _borrowState.value = BorrowState.Error("Stok buku habis")
             return
        }

        viewModelScope.launch {
            _borrowState.value = BorrowState.Loading
            try {
                // Decrease stock
                val updatedBook = currentBook.copy(stock = currentBook.stock - 1)
                bookRepository.updateBook(updatedBook)
                _book.value = updatedBook 
                
                // Create Transaction
                val transaction = Transaction(
                    userId = user.id,
                    bookId = currentBook.id,
                    borrowDate = System.currentTimeMillis(),
                    dueDate = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // 7 days
                    status = "borrowed"
                )
                transactionRepository.borrowBook(transaction)
                _borrowState.value = BorrowState.Success("Berhasil meminjam buku")
            } catch (e: Exception) {
                _borrowState.value = BorrowState.Error("Gagal meminjam: ${e.message}")
            }
        }
    }
    
    fun dismissState() {
        _borrowState.value = BorrowState.Idle
    }
}

sealed class BorrowState {
    data object Idle : BorrowState()
    data object Loading : BorrowState()
    data class Success(val message: String) : BorrowState()
    data class Error(val message: String) : BorrowState()
}

class BookDetailViewModelFactory(
    private val bookRepository: BookRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val bookId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookDetailViewModel(bookRepository, transactionRepository, userRepository, bookId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
