package com.example.unittesting.usermanager.domain.cache

import com.example.unittesting.usermanager.domain.model.User
import kotlinx.coroutines.flow.SharedFlow

interface UserCache {
    suspend fun getUser(userId: String): User?
    suspend fun putUser(user: User)
    suspend fun removeUser(userId: String)
    fun observeUsers(): SharedFlow<List<User>>
    suspend fun initializeCache(users: List<User>)
}