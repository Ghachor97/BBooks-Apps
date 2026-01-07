package com.galang.bbooks.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galang.bbooks.data.User
import com.galang.bbooks.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    var fullName by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    
    var error by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun register(onSuccess: () -> Unit) {
        if (fullName.isBlank() || username.isBlank() || password.isBlank()) {
            error = "Semua kolom harus diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                // Check if user exists happens in repository return
                val safeUsername = username.trim()
                val safePassword = password.trim()
                
                val user = User(
                    fullName = fullName,
                    username = safeUsername,
                    passwordHash = safePassword, // In real app, hash this
                    role = "user" // Default role
                )
                val success = repository.register(user)
                if (success) {
                    // Auto-login after registration
                    val loggedInUser = repository.login(safeUsername, safePassword)
                    if (loggedInUser != null) {
                        onSuccess()
                    } else {
                        error = "Gagal login otomatis setelah daftar"
                    }
                } else {
                    error = "Username sudah digunakan"
                }
            } catch (e: Exception) {
                error = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
