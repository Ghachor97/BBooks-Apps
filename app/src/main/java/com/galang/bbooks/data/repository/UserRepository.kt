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

    suspend fun login(username: String, passwordHash: String): User? {
        val user = userDao.getUserByUsername(username)
        // Mimicking hash check. In real app, passwordHash in DB should be salted hash.
        return if (user?.passwordHash == passwordHash) {
            currentUser = user
            userPreferences.saveUserId(user.id)
            user
        } else null
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
    
    suspend fun register(user: User): Boolean {
        if (userDao.getUserByUsername(user.username) != null) {
            return false // Username already exists
        }
        userDao.insertUser(user)
        return true
    }

    suspend fun ensureAdminExists() {
        if (userDao.getUserByUsername("admin") == null) {
            userDao.insertUser(User(username = "admin", passwordHash = "admin", role = "admin", fullName = "Administrator"))
        }
        if (userDao.getUserByUsername("user") == null) {
            userDao.insertUser(User(username = "user", passwordHash = "user", role = "user", fullName = "Anggota Perpustakaan"))
        }
    }
    
    // Helper to fetch user by ID for auto-login
    suspend fun getUserById(id: Int): User? {
         return userDao.getUserById(id)
    }
}
