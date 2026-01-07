package com.galang.bbooks.data.repository

import com.galang.bbooks.data.Transaction
import com.galang.bbooks.data.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun getTransactionsByUser(userId: Int): Flow<List<Transaction>> = transactionDao.getTransactionsByUser(userId)
    
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun borrowBook(transaction: Transaction) = transactionDao.insertTransaction(transaction)

    suspend fun returnBook(transaction: Transaction) = transactionDao.updateTransaction(transaction)
}
