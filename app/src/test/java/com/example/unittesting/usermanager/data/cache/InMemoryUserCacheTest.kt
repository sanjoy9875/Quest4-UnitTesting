package com.example.unittesting.usermanager.data.cache

import app.cash.turbine.test
import com.example.unittesting.usermanager.domain.model.User
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class InMemoryUserCacheTest {
 private lateinit var cache: InMemoryUserCache

 @Before
 fun setup() {
  cache = InMemoryUserCache()
 }

 @Test
 fun `getUser should return user if exists`() = runTest {
  val user = User("1", "Sanjoy", "sanjoy@example.com")
  cache.putUser(user)

  val result = cache.getUser("1")
  assertThat(result).isEqualTo(user)
 }

 @Test
 fun `getUser should return null if user does not exist`() = runTest {
  val result = cache.getUser("unknown")
  assertThat(result).isNull()
 }

 @Test
 fun `putUser should add user and emit updated list`() = runTest {
  val user = User("1", "User", "user@example.com")

  cache.observeUsers().test {
   // Consume initial emission (empty list)
   val initial = awaitItem()
   assertThat(initial).isEmpty()

   // Perform the action
   cache.putUser(user)

   // Now assert on the updated list
   val result = awaitItem()
   assertThat(result).containsExactly(user)

   cancelAndIgnoreRemainingEvents()
  }
 }


 @Test
 fun `removeUser should remove user and emit updated list`() = runTest {
  val user = User("1", "Sanjoy", "sanjoy@example.com")
  cache.putUser(user)

  cache.observeUsers().test {
   skipItems(1)
   cache.removeUser("1")
   val result = awaitItem()
   assertThat(result).isEmpty()
  }
 }

 @Test
 fun `observeUsers should emit current and subsequent lists`() = runTest {
  val user1 = User("1", "User1", "user1@example.com")
  val user2 = User("2", "User2", "user2@example.com")

  cache.observeUsers().test {
   val initial = awaitItem()
   assertThat(initial).isEmpty()

   cache.putUser(user1)
   assertThat(awaitItem()).containsExactly(user1)

   cache.putUser(user2)
   assertThat(awaitItem()).containsExactly(user1, user2)
  }
 }

 @Test
 fun `initializeCache should clear existing and populate with new users`() = runTest {
  val user = User("1", "User1", "user1@example.com")
  cache.putUser(user)

  val newUsers = listOf(
   User("2", "User2", "user2@example.com"),
   User("3", "User3", "user3@example.com")
  )

  cache.observeUsers().test {
   skipItems(1)
   cache.initializeCache(newUsers)
   val result = awaitItem()
   assertThat(result).containsExactlyElementsIn(newUsers)
  }
 }
}