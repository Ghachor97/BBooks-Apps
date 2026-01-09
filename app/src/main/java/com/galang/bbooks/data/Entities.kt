package com.galang.bbooks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val role: String, // "admin", "user"
    val fullName: String,
    val photoUrl: String? = null
)

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val category: String,
    val stock: Int,
    val coverUrl: String, // Can be placeholder URL or resource ID mapped
    val isAvailable: Boolean = true
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Int,
    val bookId: Long,
    val borrowDate: Long,
    val returnDate: Long? = null,
    val dueDate: Long,
    val status: String, // "borrowed", "returned"
    val fine: Double = 0.0,
    val returnCondition: String? = null // "Baik", "Rusak", "Hilang", "Robek"
)
