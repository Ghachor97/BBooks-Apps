package com.galang.bbooks.data.repository

import com.galang.bbooks.data.User
import com.galang.bbooks.data.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    var currentUser: User? = null
        private set

    suspend fun login(username: String, passwordHash: String): User? {
        val user = userDao.getUserByUsername(username)
        // Mimicking hash check. In real app, passwordHash in DB should be salted hash.
        return if (user?.passwordHash == passwordHash) {
            currentUser = user
            user
        } else null
    }

    fun logout() {
        currentUser = null
    }

    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun register(user: User) {
        userDao.insertUser(user)
    }

    suspend fun ensureAdminExists() {
        if (userDao.getUserByUsername("admin") == null) {
            userDao.insertUser(User(username = "admin", passwordHash = "admin", role = "admin", fullName = "Administrator"))
        }
        if (userDao.getUserByUsername("user") == null) {
            userDao.insertUser(User(username = "user", passwordHash = "user", role = "user", fullName = "Anggota Perpustakaan"))
        }
    }
}
