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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class TransactionWithBook(
    val transaction: Transaction,
    val book: Book,
    val fineEstimate: Double
)

class ReturnBookViewModel(
    private val transactionRepository: TransactionRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = userRepository.currentUser
    
    val activeTransactions: StateFlow<List<TransactionWithBook>> = combine(
        transactionRepository.getTransactionsByUser(_currentUser?.id ?: -1),
        bookRepository.allBooks
    ) { transactions, books ->
        transactions.filter { it.status == "borrowed" }.mapNotNull { transaction ->
            val book = books.find { it.id == transaction.bookId } ?: return@mapNotNull null
            val fine = calculateFine(transaction.dueDate)
            TransactionWithBook(transaction, book, fine)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _returnState = MutableStateFlow<BorrowState>(BorrowState.Idle)
    val returnState = _returnState

    fun returnBook(item: TransactionWithBook) {
        viewModelScope.launch {
            _returnState.value = BorrowState.Loading
            try {
                // 1. Update Transaction
                val now = System.currentTimeMillis()
                val updatedTransaction = item.transaction.copy(
                    returnDate = now,
                    status = "returned",
                    fine = item.fineEstimate
                )
                transactionRepository.returnBook(updatedTransaction)

                // 2. Update Book Stock
                val updatedBook = item.book.copy(stock = item.book.stock + 1)
                bookRepository.updateBook(updatedBook)

                _returnState.value = BorrowState.Success("Buku berhasil dikembalikan. Denda: Rp ${item.fineEstimate.toInt()}")
            } catch (e: Exception) {
                _returnState.value = BorrowState.Error("Gagal mengembalikan buku: ${e.message}")
            }
        }
    }

    fun dismissState() {
        _returnState.value = BorrowState.Idle
    }

    private fun calculateFine(dueDate: Long): Double {
        val now = System.currentTimeMillis()
        if (now <= dueDate) return 0.0
        val diff = now - dueDate
        val daysLate = TimeUnit.MILLISECONDS.toDays(diff)
        return (daysLate * 1000.0).coerceAtLeast(0.0) // 1000 IDR per day
    }
}

class ReturnBookViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReturnBookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReturnBookViewModel(transactionRepository, bookRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
