# Quest4

Key Concepts Covered by this Task:
Unit Test Structure: @Before, @After, @Test annotations, test classes.

Dependency Mocking with MockK:

mockk(): Creating mock objects.
coEvery { ... } returns ...: Stubbing suspend functions.
coJustRun { ... }: Stubbing suspend functions that return Unit.
coVerify { ... }: Verifying interactions with suspend functions.
coVerifyOrder { ... }: Verifying the order of interactions.
clearAllMocks(): Resetting mock state.
Asynchronous Testing with Kotlin Coroutines:
runTest (from kotlinx-coroutines-test): A test scope that manages coroutine execution, making asynchronous code synchronous for testing.
StandardTestDispatcher: A test dispatcher that allows explicit control over coroutine execution within runTest.
Dispatchers.setMain/resetMain: For testing code that uses Dispatchers.Main.

Flow Testing with Turbine:

flow.test { ... }: Collecting and asserting on Flow emissions.
awaitItem(): Waiting for and asserting the next emitted item.
awaitComplete(): Waiting for the Flow to complete.
cancelAndIgnoreRemainingEvents(): Stopping observation and ignoring further emissions.

Assertions with Google Truth:

assertThat(actual).isEqualTo(expected)
assertThat(actual).isTrue() / isFalse()
assertThat(collection).containsExactly(...)
assertThat(collection).isEmpty() / isNotEmpty()
Chainable assertions for readability.

Best Practices:

Isolation: Testing one unit (e.g., UserManager) in isolation from its dependencies by mocking.
Readable Tests: Clear arrangement (Arrange-Act-Assert structure).
Descriptive Test Names: Naming tests to convey their purpose (methodName_should_behaviorWhenCondition).
Fast Tests: Unit tests should be fast, avoiding actual network or database calls.
Comprehensive Coverage: Testing various scenarios, including happy paths, edge cases, and error conditions.
