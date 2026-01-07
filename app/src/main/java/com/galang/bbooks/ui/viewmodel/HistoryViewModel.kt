package com.galang.bbooks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galang.bbooks.data.repository.BookRepository
import com.galang.bbooks.data.repository.TransactionRepository
import com.galang.bbooks.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.galang.bbooks.data.Book
import com.galang.bbooks.data.Transaction
import com.galang.bbooks.data.User

class HistoryViewModel(
    private val transactionRepository: TransactionRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = userRepository.currentUser
    
    val history: StateFlow<List<TransactionWithBook>> = combine(
        if (_currentUser?.role == "admin") transactionRepository.getAllTransactions() else transactionRepository.getTransactionsByUser(_currentUser?.id ?: -1),
        bookRepository.allBooks,
        userRepository.allUsers
    ) { transactions: List<Transaction>, books: List<Book>, users: List<User> ->
        transactions.sortedByDescending { it.borrowDate }.mapNotNull { transaction ->
            val book = books.find { it.id == transaction.bookId } ?: return@mapNotNull null
            // Fine is stored in transaction if returned, or calculated if active.
            // For history, show stored fine if returned.
            val fine = if (transaction.status == "returned") transaction.fine else 0.0
            val borrower = users.find { it.id == transaction.userId }
            TransactionWithBook(transaction, book, fine, borrower?.fullName)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

class HistoryViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(transactionRepository, bookRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
