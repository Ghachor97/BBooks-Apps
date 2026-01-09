package com.galang.bbooks.ui.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galang.bbooks.data.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GoogleSignInViewModel(
    private val userRepository: UserRepository,
    private val webClientId: String
) : ViewModel() {
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    private val firebaseAuth = FirebaseAuth.getInstance()
    
    /**
     * Create GoogleSignInClient - call this from Activity/Screen
     */
    fun getGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()
        
        return GoogleSignIn.getClient(activity, gso)
    }
    
    /**
     * Get the sign-in intent to start the Google Sign-In flow
     */
    fun getSignInIntent(activity: Activity): Intent {
        isLoading = true
        errorMessage = null
        return getGoogleSignInClient(activity).signInIntent
    }
    
    /**
     * Handle the result from Google Sign-In activity
     */
    fun handleSignInResult(result: ActivityResult, onSuccess: () -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account, onSuccess)
            } catch (e: ApiException) {
                isLoading = false
                errorMessage = "Google Sign-In gagal: ${getErrorMessage(e.statusCode)}"
            }
        } else {
            isLoading = false
            if (result.resultCode == Activity.RESULT_CANCELED) {
                errorMessage = "Sign-In dibatalkan"
            } else {
                errorMessage = "Sign-In gagal (code: ${result.resultCode})"
            }
        }
    }
    
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                
                if (firebaseUser != null) {
                    // Save user to local database
                    userRepository.signInWithGoogle(
                        email = firebaseUser.email ?: account.email ?: "",
                        displayName = firebaseUser.displayName ?: account.displayName ?: "Pengguna",
                        photoUrl = firebaseUser.photoUrl?.toString() ?: account.photoUrl?.toString()
                    )
                    
                    isLoading = false
                    onSuccess()
                } else {
                    isLoading = false
                    errorMessage = "Gagal mendapatkan data pengguna dari Firebase"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Autentikasi Firebase gagal: ${e.message}"
            }
        }
    }
    
    private fun getErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            7 -> "Network error - periksa koneksi internet"
            12500 -> "Google Play Services error"
            12501 -> "Sign-In dibatalkan"
            12502 -> "Sign-In sedang berjalan"
            10 -> "Developer error - SHA-1 atau Client ID tidak valid"
            else -> "Error code: $statusCode"
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
}

class GoogleSignInViewModelFactory(
    private val userRepository: UserRepository,
    private val webClientId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoogleSignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoogleSignInViewModel(userRepository, webClientId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
