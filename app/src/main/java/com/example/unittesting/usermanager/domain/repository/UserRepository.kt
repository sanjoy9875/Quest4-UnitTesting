package com.example.unittesting.usermanager.domain.repository

import com.example.unittesting.usermanager.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUserById(userId: String): User?
    suspend fun fetchAllUsers(): Flow<User>
    suspend fun saveUser(user: User): User
    suspend fun deleteUser(userId: String): Boolean
}