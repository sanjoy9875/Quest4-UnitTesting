package com.example.unittesting.usermanager.data.repository

import com.example.unittesting.usermanager.domain.model.User
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class InMemoryUserRepositoryTest {
 private lateinit var repository: InMemoryUserRepository

 @Before
 fun setup() {
  repository = InMemoryUserRepository()
 }

 @Test
 fun `fetchUserById should return user if exists`() = runTest {
  val user = User("1", "User", "user@example.com")
  repository.saveUser(user)

  val result = repository.fetchUserById("1")
  assertThat(result).isEqualTo(user)
 }

 @Test
 fun `fetchUserById should return null if user does not exist`() = runTest {
  val result = repository.fetchUserById("unknown")
  assertThat(result).isNull()
 }

 @Test
 fun `fetchAllUsers should return all saved users`() = runTest {
  val users = listOf(
   User("1", "A", "a@mail.com"),
   User("2", "B", "b@mail.com")
  )
  users.forEach { repository.saveUser(it) }

  val result = repository.fetchAllUsers().toList()
  assertThat(result).containsExactlyElementsIn(users)
 }

 @Test
 fun `saveUser should add or update user`() = runTest {
  val user = User("1", "Initial", "init@mail.com")
  repository.saveUser(user)

  val updated = User("1", "Updated", "update@mail.com")
  repository.saveUser(updated)

  val result = repository.fetchUserById("1")
  assertThat(result).isEqualTo(updated)
 }

 @Test
 fun `deleteUser should remove user`() = runTest {
  val user = User("1", "Delete", "delete@mail.com")
  repository.saveUser(user)
  val deleted = repository.deleteUser("1")
  val result = repository.fetchUserById("1")

  assertThat(deleted).isTrue()
  assertThat(result).isNull()
 }

 @Test
 fun `deleteUser should return false if user not found`() = runTest {
  val result = repository.deleteUser("nonexistent")
  assertThat(result).isFalse()
 }
}