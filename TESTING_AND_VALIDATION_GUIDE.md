# TESTING AND VALIDATION GUIDE - Agente-Smith-Android

## Overview

Comprehensive testing strategy for the Agente-Smith-Android project, covering unit tests, integration tests, UI tests, and validation procedures.

## Table of Contents

1. [Unit Testing](#unit-testing)
2. [Integration Testing](#integration-testing)
3. [UI Testing](#ui-testing)
4. [Manual Testing](#manual-testing)
5. [Performance Testing](#performance-testing)
6. [Security Testing](#security-testing)

---

## Unit Testing

### Setup

```gradle
dependencies {
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'androidx.test:core:1.5.0'
    testImplementation 'io.mockk:mockk:1.13.5'
    testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1'
}
```

### ViewModel Testing

```kotlin
class ChatViewModelTest {
    private lateinit var viewModel: ChatViewModel
    private val repository = mockk<ChatRepository>()
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @Before
    fun setUp() {
        viewModel = ChatViewModel(repository)
    }
    
    @Test
    fun `test sendMessage updates ui state`() = runTest {
        // Arrange
        val message = "Hello"
        coEvery { repository.sendMessage(message) } returns Result.success("OK")
        
        // Act
        viewModel.sendMessage(message)
        
        // Assert
        assert(viewModel.uiState.value is ChatUiState.Success)
    }
}
```

### Repository Testing

```kotlin
class ChatRepositoryTest {
    private lateinit var repository: ChatRepository
    private val localDataSource = mockk<ChatLocalDataSource>()
    private val remoteDataSource = mockk<ChatRemoteDataSource>()
    
    @Before
    fun setUp() {
        repository = ChatRepositoryImpl(localDataSource, remoteDataSource)
    }
    
    @Test
    fun `test getMessages returns data from local source`() = runTest {
        // Arrange
        val messages = listOf(ChatMessage("Test"))
        coEvery { localDataSource.getMessages() } returns messages
        
        // Act
        val result = repository.getMessages()
        
        // Assert
        assertEquals(messages, result)
    }
}
```

---

## Integration Testing

### Database Integration Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatDatabaseTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: ChatDao
    
    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        dao = database.chatDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun testInsertAndRetrieveMessage() = runBlocking {
        val message = ChatMessage(text = "Test")
        dao.insertMessage(message)
        
        val retrieved = dao.getMessage(message.id)
        assertEquals(message.text, retrieved?.text)
    }
}
```

---

## UI Testing

### Setup

```gradle
dependencies {
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation 'androidx.test:runner:1.5.2'
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4:1.5.0'
    androidTestImplementation 'androidx.test.uiautomator:uiautomator:2.2.0'
}
```

### Compose UI Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testChatScreenDisplaysMessages() {
        composeTestRule.setContent {
            ChatScreen(
                messages = listOf(
                    ChatMessage(text = "Hello"),
                    ChatMessage(text = "World")
                )
            )
        }
        
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
        composeTestRule.onNodeWithText("World").assertIsDisplayed()
    }
    
    @Test
    fun testInputFieldAcceptsText() {
        composeTestRule.setContent { ChatScreen() }
        
        composeTestRule.onNodeWithTag("messageInput")
            .performTextInput("Test message")
        
        composeTestRule.onNodeWithTag("messageInput")
            .assertTextEquals("Test message")
    }
}
```

---

## Manual Testing

### Checklist

- [ ] App launches without crashes
- [ ] Chat screen displays correctly
- [ ] Messages send successfully
- [ ] Messages display in correct order
- [ ] User can scroll through messages
- [ ] Input field accepts text
- [ ] Send button is functional
- [ ] Navigation works correctly
- [ ] App handles network errors gracefully
- [ ] Offline mode works
- [ ] Database persists messages
- [ ] UI is responsive

### Testing Scenarios

**Scenario 1: Basic Chat Flow**
1. Launch app
2. Type message
3. Send message
4. Verify message appears
5. Restart app
6. Verify message persists

**Scenario 2: Network Error Handling**
1. Disable network
2. Attempt to send message
3. Verify error message appears
4. Enable network
5. Retry sending
6. Verify message sent successfully

---

## Performance Testing

### Memory Profiling

```kotlin
// Monitor memory usage
@Test
fun testMemoryUsage() {
    val runtime = Runtime.getRuntime()
    val usedBefore = runtime.totalMemory() - runtime.freeMemory()
    
    // Run operation
    performHeavyOperation()
    
    val usedAfter = runtime.totalMemory() - runtime.freeMemory()
    val memoryIncrease = usedAfter - usedBefore
    
    // Assert memory increase is acceptable
    assert(memoryIncrease < 10_000_000) // 10MB threshold
}
```

### Benchmark Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatScreenBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()
    
    @Test
    fun benchmarkMessageRendering() {
        val messages = (1..1000).map { ChatMessage("Message $it") }
        
        benchmarkRule.measureRepeated {
            renderMessages(messages)
        }
    }
}
```

---

## Security Testing

### Input Validation

```kotlin
@Test
fun testInputValidation() {
    val validator = InputValidator()
    
    // Test empty input
    assert(!validator.isValid(""))
    
    // Test valid input
    assert(validator.isValid("Valid message"))
    
    // Test XSS injection
    assert(!validator.isValid("<script>alert('xss')</script>"))
}
```

### Data Encryption

```kotlin
@Test
fun testDataEncryption() {
    val encryptor = DataEncryptor()
    val plaintext = "Sensitive data"
    
    val encrypted = encryptor.encrypt(plaintext)
    val decrypted = encryptor.decrypt(encrypted)
    
    assertEquals(plaintext, decrypted)
    assertNotEquals(plaintext, encrypted)
}
```

---

## Continuous Integration

### GitHub Actions Workflow

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run Unit Tests
        run: ./gradlew test
      - name: Run Android Tests
        run: ./gradlew connectedAndroidTest
      - name: Upload Coverage
        uses: codecov/codecov-action@v3
```

---

## Test Coverage Goals

- Unit Tests: 80% coverage
- Integration Tests: 60% coverage
- UI Tests: Critical user paths
- Overall: 70% code coverage

---

## Reporting Issues

When a test fails:

1. Document the failure
2. Check for environment issues
3. Review recent code changes
4. Create a bug report if needed
5. Fix and re-run tests

---

**Version**: 1.2.0
**Last Updated**: 2024
