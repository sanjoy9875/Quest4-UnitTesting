package com.example.unittesting.usermanager.manager

import app.cash.turbine.test
import com.example.unittesting.usermanager.domain.cache.UserCache
import com.example.unittesting.usermanager.domain.model.User
import com.example.unittesting.usermanager.domain.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserManagerTest {

 private lateinit var userManager: UserManager
 private val userRepository: UserRepository = mockk()
 private val userCache: UserCache = mockk(relaxed = true)
 private val testDispatcher = StandardTestDispatcher()
 private val testUser = User("1", "Test User", "test@example.com")

 @Before
 fun setup() {
  userManager = UserManager(userRepository, userCache)
 }

 @After
 fun tearDown() {
  clearAllMocks()
 }

 @Test
 fun `getUser should return user from cache if available`() = runTest(testDispatcher) {
  coEvery { userCache.getUser("1") } returns testUser

  val result = userManager.getUser("1")

  assertThat(result).isEqualTo(testUser)
  coVerify(exactly = 0) { userRepository.fetchUserById(any()) }
 }

 @Test
 fun `getUser should fetch user from repository and cache it if not in cache`() = runTest(testDispatcher) {
  coEvery { userCache.getUser("1") } returns null
  coEvery { userRepository.fetchUserById("1") } returns testUser

  val result = userManager.getUser("1")

  assertThat(result).isEqualTo(testUser)
  coVerifySequence {
   userCache.getUser("1")
   userRepository.fetchUserById("1")
   userCache.putUser(testUser)
  }
 }

 @Test
 fun `getUser should return null if user not found in cache or repository`() = runTest(testDispatcher) {
  coEvery { userCache.getUser("1") } returns null
  coEvery { userRepository.fetchUserById("1") } returns null

  val result = userManager.getUser("1")

  assertThat(result).isNull()
 }

 @Test
 fun `refreshAllUsers should fetch from repository and update cache`() = runTest(testDispatcher) {
  coEvery { userRepository.fetchAllUsers() } returns flowOf(testUser)

  val result = userManager.refreshAllUsers().toList(mutableListOf())

  assertThat(result).containsExactly(testUser)
 }

 @Test
 fun `observeAllUsers should emit current cached users`() = runTest(testDispatcher) {
  val sharedFlow = MutableSharedFlow<List<User>>(replay = 1)
  sharedFlow.emit(listOf(testUser))
  every { userCache.observeUsers() } returns sharedFlow

  userManager.observeAllUsers().test {
   assertThat(awaitItem()).containsExactly(testUser)
   cancelAndIgnoreRemainingEvents()
  }
 }

 @Test
 fun `saveUser should save to repository and update cache`() = runTest(testDispatcher) {
  coEvery { userRepository.saveUser(testUser) } returns testUser

  val result = userManager.saveUser(testUser)

  assertThat(result).isEqualTo(testUser)
  coVerifyOrder {
   userRepository.saveUser(testUser)
   userCache.putUser(testUser)
  }
 }

 @Test
 fun `deleteUser should delete from repository and remove from cache`() = runTest(testDispatcher) {
  coEvery { userRepository.deleteUser("1") } returns true

  val result = userManager.deleteUser("1")

  assertThat(result).isTrue()
  coVerifySequence {
   userRepository.deleteUser("1")
   userCache.removeUser("1")
  }
 }

 @Test
 fun `deleteUser should handle failure from repository`() = runTest(testDispatcher) {
  coEvery { userRepository.deleteUser("1") } returns false

  val result = userManager.deleteUser("1")

  assertThat(result).isFalse()
  coVerify(exactly = 0) { userCache.removeUser(any()) }
 }
}