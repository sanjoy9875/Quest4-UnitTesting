package com.example.unittesting.usermanager.manager

import com.example.unittesting.usermanager.domain.cache.UserCache
import com.example.unittesting.usermanager.domain.model.User
import com.example.unittesting.usermanager.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

class UserManager(
    private val userRepository: UserRepository,
    private val userCache: UserCache
) {
    suspend fun getUser(userId: String): User? {
        return userCache.getUser(userId) ?: userRepository.fetchUserById(userId)?.also {
            userCache.putUser(it)
        }
    }

    suspend fun refreshAllUsers(): Flow<User> {
        val users = userRepository.fetchAllUsers().toList()
        userCache.initializeCache(users)
        return users.asFlow()
    }

    fun observeAllUsers(): SharedFlow<List<User>> = userCache.observeUsers()

    suspend fun saveUser(user: User): User {
        val saved = userRepository.saveUser(user)
        userCache.putUser(saved)
        return saved
    }

    suspend fun deleteUser(userId: String): Boolean {
        val deleted = userRepository.deleteUser(userId)
        if (deleted) userCache.removeUser(userId)
        return deleted
    }
}