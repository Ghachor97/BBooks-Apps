package com.galang.bbooks.data.repository

import com.galang.bbooks.data.User
import com.galang.bbooks.data.UserDao
import kotlinx.coroutines.flow.Flow
import com.galang.bbooks.data.UserPreferences

class UserRepository(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {
    var currentUser: User? = null
        private set

    companion object {
        // Admin emails - hardcoded as requested
        private val ADMIN_EMAILS = setOf(
            "anarkiboy97@gmail.com",
            "gustiadityamuzaky08@gmail.com"
        )
    }

    /**
     * Sign in with Google - creates or updates user in local database
     */
    suspend fun signInWithGoogle(email: String, displayName: String, photoUrl: String?): User {
        val role = if (email.lowercase() in ADMIN_EMAILS.map { it.lowercase() }) "admin" else "user"
        
        // Check if user exists
        var user = userDao.getUserByEmail(email)
        
        if (user != null) {
            // Update existing user
            user = user.copy(
                fullName = displayName,
                photoUrl = photoUrl,
                role = role // Role can change if email is added/removed from admin list
            )
            userDao.upsertUser(user)
        } else {
            // Create new user
            user = User(
                email = email,
                fullName = displayName,
                photoUrl = photoUrl,
                role = role
            )
            val newId = userDao.upsertUser(user)
            user = user.copy(id = newId.toInt())
        }
        
        currentUser = user
        userPreferences.saveUserId(user.id)
        return user
    }

    fun logout() {
        currentUser = null
        userPreferences.clearSession()
    }

    suspend fun tryAutoLogin(): Boolean {
        val userId = userPreferences.getUserId() ?: return false
        val user = userDao.getUserById(userId)
        return if (user != null) {
            currentUser = user
            true
        } else {
            userPreferences.clearSession()
            false
        }
    }

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    // Helper to fetch user by ID
    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
    
    // Helper to check if current user is admin
    fun isCurrentUserAdmin(): Boolean {
        return currentUser?.role == "admin"
    }
}
