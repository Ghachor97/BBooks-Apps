package com.galang.bbooks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galang.bbooks.data.Book
import com.galang.bbooks.data.repository.BookRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageBookViewModel(private val repository: BookRepository) : ViewModel() {

    val books = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBook(title: String, author: String, category: String, stock: Int) {
        viewModelScope.launch {
            repository.addBook(
                Book(title = title, author = author, category = category, stock = stock, coverUrl = "")
            )
        }
    }

    fun updateBook(book: Book, title: String, author: String, category: String, stock: Int) {
        viewModelScope.launch {
            repository.updateBook(book.copy(
                title = title,
                author = author,
                category = category,
                stock = stock
            ))
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }
}

class ManageBookViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageBookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ManageBookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
