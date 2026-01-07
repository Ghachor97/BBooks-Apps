package com.galang.bbooks.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galang.bbooks.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    init {
        // Ensure default users exist
        viewModelScope.launch {
            repository.ensureAdminExists()
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            loginError = "Username dan password harus diisi"
            return
        }
        
        viewModelScope.launch {
            isLoading = true
            loginError = null
            
            val safeUsername = username.trim()
            val safePassword = password.trim() // Optional but good for this simple app
            
            val user = repository.login(safeUsername, safePassword)
            isLoading = false
            
            if (user != null) {
                onSuccess()
            } else {
                loginError = "Username atau password salah"
            }
        }
    }
}

class LoginViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
