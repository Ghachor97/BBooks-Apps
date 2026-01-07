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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.galang.bbooks.data.Transaction
import com.galang.bbooks.data.User
import com.galang.bbooks.ui.viewmodel.TransactionWithBook

class DashboardViewModel(
    private val bookRepository: BookRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val totalBooks: StateFlow<Int> = bookRepository.allBooks
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val availableBooks: StateFlow<Int> = bookRepository.allBooks
        .map { list -> list.count { it.stock > 0 && it.isAvailable } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _currentUser = userRepository.currentUser

    val recentActivity: StateFlow<List<TransactionWithBook>> = combine(
        if (_currentUser?.role == "admin") transactionRepository.getAllTransactions() else transactionRepository.getTransactionsByUser(_currentUser?.id ?: -1),
        bookRepository.allBooks,
        userRepository.allUsers
    ) { transactions, books, users ->
        transactions.sortedByDescending { it.borrowDate }
            .take(5) // Limit to 5 most recent
            .mapNotNull { transaction ->
                val book = books.find { it.id == transaction.bookId } ?: return@mapNotNull null
                val fine = if (transaction.status == "returned") transaction.fine else 0.0
                val borrower = users.find { it.id == transaction.userId }
                TransactionWithBook(transaction, book, fine, borrower?.fullName)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            bookRepository.ensureBooksExist()
        }
    }
}

class DashboardViewModelFactory(
    private val bookRepository: BookRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(bookRepository, transactionRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
