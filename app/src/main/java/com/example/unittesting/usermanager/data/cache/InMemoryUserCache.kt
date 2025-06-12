package com.example.unittesting.usermanager.data.cache

import com.example.unittesting.usermanager.domain.cache.UserCache
import com.example.unittesting.usermanager.domain.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryUserCache : UserCache {
    private val users = mutableMapOf<String, User>()
    private val _usersFlow = MutableSharedFlow<List<User>>(replay = 1)
    private val mutex = Mutex()

    init {
        _usersFlow.tryEmit(emptyList())
    }

    override suspend fun getUser(userId: String): User? = mutex.withLock {
        users[userId]
    }

    override suspend fun putUser(user: User) = mutex.withLock {
        users[user.id] = user
        _usersFlow.emit(users.values.toList())
    }

    override suspend fun removeUser(userId: String) = mutex.withLock {
        if (users.remove(userId) != null) {
            _usersFlow.emit(users.values.toList())
        }
    }

    override fun observeUsers(): SharedFlow<List<User>> = _usersFlow.asSharedFlow()

    override suspend fun initializeCache(users: List<User>) = mutex.withLock {
        this.users.clear()
        users.forEach { this.users[it.id] = it }
        _usersFlow.emit(this.users.values.toList())
    }

    suspend fun clear() = mutex.withLock {
        users.clear()
        _usersFlow.tryEmit(emptyList())
    }
}