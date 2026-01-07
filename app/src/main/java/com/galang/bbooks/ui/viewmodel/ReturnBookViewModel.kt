package com.galang.bbooks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galang.bbooks.data.Book
import com.galang.bbooks.data.Transaction
import com.galang.bbooks.data.repository.BookRepository
import com.galang.bbooks.data.repository.TransactionRepository
import com.galang.bbooks.data.repository.UserRepository
import com.galang.bbooks.data.User
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
    val fineEstimate: Double,
    val borrowerName: String? = null
)

class ReturnBookViewModel(
    private val transactionRepository: TransactionRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = userRepository.currentUser
    
    val activeTransactions: StateFlow<List<TransactionWithBook>> = combine(
        if (_currentUser?.role == "admin") transactionRepository.getAllTransactions() else transactionRepository.getTransactionsByUser(_currentUser?.id ?: -1),
        bookRepository.allBooks,
        userRepository.allUsers
    ) { transactions: List<Transaction>, books: List<Book>, users: List<User> ->
        transactions.filter { it.status == "borrowed" }.mapNotNull { transaction ->
            val book = books.find { it.id == transaction.bookId } ?: return@mapNotNull null
            val fine = calculateFine(transaction.dueDate)
            val borrower = users.find { it.id == transaction.userId }
            TransactionWithBook(transaction, book, fine, borrower?.fullName)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _returnState = MutableStateFlow<BorrowState>(BorrowState.Idle)
    val returnState = _returnState

    companion object {
        const val FINE_RUSAK = 100000.0
        const val FINE_HILANG = 150000.0
        const val FINE_ROBEK = 50000.0
    }

    fun returnBook(item: TransactionWithBook, condition: String = "Baik") {
        viewModelScope.launch {
            _returnState.value = BorrowState.Loading
            try {
                // Calculate Condition Fine
                val conditionFine = when (condition) {
                    "Rusak" -> FINE_RUSAK
                    "Hilang" -> FINE_HILANG
                    "Robek" -> FINE_ROBEK
                    else -> 0.0
                }

                // Total Fine = Overdue Fine + Condition Fine
                val totalFine = item.fineEstimate + conditionFine

                // 1. Update Transaction
                val now = System.currentTimeMillis()
                val updatedTransaction = item.transaction.copy(
                    returnDate = now,
                    status = "returned",
                    fine = totalFine,
                    returnCondition = condition
                )
                transactionRepository.returnBook(updatedTransaction)

                // 2. Update Book Stock (Only if NOT 'Hilang')
                if (condition != "Hilang") {
                    val updatedBook = item.book.copy(stock = item.book.stock + 1)
                    bookRepository.updateBook(updatedBook)
                }

                val message = if (totalFine > 0) {
                     "Buku dikembalikan dengan status $condition.\nTotal Denda: Rp ${totalFine.toInt()}\nHarap lakukan pembayaran!"
                } else {
                     "Buku berhasil dikembalikan. Status: $condition."
                }

                _returnState.value = BorrowState.Success(message)
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
