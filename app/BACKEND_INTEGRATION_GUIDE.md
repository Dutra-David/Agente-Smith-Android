# Backend Integration Guide - Agente Smith Android

## Overview
This guide explains how the Agente Smith Android app integrates with the Agent-S backend service. The integration provides comprehensive AI-powered agent capabilities for Android devices.

## Architecture Components

### 1. Network Layer (`network/`)
- **ApiService.kt**: Retrofit interface defining all backend API endpoints
- **RetrofitClient.kt**: Singleton managing Retrofit instance and HTTP client configuration

### 2. Data Models (`data/models/`)
- **ApiModels.kt**: Data classes for all API request/response objects
  - MessageRequest/Response
  - ChatHistory/ChatMessage
  - NLPRequest/Response
  - IntentRequest/Response
  - EntityRequest/Response
  - VoiceCommandRequest/Response
  - TaskRequest/Response
  - SessionRequest/Response

### 3. Repository Pattern (`data/repository/`)
- **ApiRepository.kt**: Repository class providing high-level API operations
  - Encapsulates all API calls
  - Handles callbacks and error management
  - Provides methods for chat, NLP, voice commands, tasks, and sessions

## API Endpoints

### Chat Operations
```
POST /api/v1/chat/process - Process user messages
GET /api/v1/chat/history - Retrieve chat history
POST /api/v1/chat/clear - Clear chat messages
```

### NLP Operations
```
POST /api/v1/nlp/process - Process text with NLP
POST /api/v1/nlp/intent_classification - Classify user intent
POST /api/v1/nlp/entity_extraction - Extract entities from text
```

### Voice Commands
```
POST /api/v1/voice/process - Process voice commands
```

### Task Management
```
POST /api/v1/tasks/create - Create new task
GET /api/v1/tasks/list - List all tasks
POST /api/v1/tasks/execute - Execute task
```

### Session Management
```
POST /api/v1/session/create - Create new session
POST /api/v1/session/update - Update session metadata
```

## Usage Example

```kotlin
// Initialize
val apiService = RetrofitClient.apiService
val repository = ApiRepository(apiService, context)

// Process a message
repository.processMessage(
    sessionId = "session_123",
    message = "What's the weather?"
) { response ->
    // Handle response
    val intent = response.body()?.intent
    val entities = response.body()?.entities
}

// Classify intent
repository.classifyIntent("Turn on the lights") { response ->
    val intent = response.body()?.intent
    val confidence = response.body()?.confidence
}

// Extract entities
repository.extractEntities("I want pizza from downtown") { response ->
    val entities = response.body()?.entities
}
```

## Configuration

### Base URL
The API base URL is configured in RetrofitClient.kt:
```kotlin
private const val BASE_URL = "http://localhost:8000/"
```

Modify this to match your backend service URL:
- Development: `http://localhost:8000/`
- Production: `https://api.yourdomain.com/`

### Timeout Settings
- Connection timeout: 30 seconds
- Read timeout: 30 seconds
- Write timeout: 30 seconds

## Next Steps for Full Implementation

1. **Create ViewModel Layer**
   - Implement chat ViewModel with LiveData
   - Add error handling and state management

2. **UI Integration**
   - Connect chat screen to repository
   - Implement message display and input
   - Add voice input UI

3. **Local Storage**
   - Add Room database for offline support
   - Implement message caching

4. **Error Handling**
   - Add retry logic
   - Implement user-friendly error messages

5. **Testing**
   - Create unit tests for repository
   - Add integration tests
   - Mock API responses

6. **Dependencies Required**
Add to build.gradle.kts:
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.9.1'
implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'
implementation 'com.google.code.gson:gson:2.8.8'
```

## API Response Format

All responses follow this structure:
```json
{
  "status": "success|error",
  "data": { /* response specific data */ },
  "error": "error message if applicable"
}
```

## Common Operations

### Start Chat Session
```kotlin
repository.createSession("user_123") { response ->
    val sessionId = response.body()?.sessionId
}
```

### Process Voice Input
```kotlin
val audioData = captureAudioFromMicrophone() // Your audio capture logic
repository.processVoiceCommand(audioData) { response ->
    val command = response.body()?.command
    executeCommand(command)
}
```

### Create Scheduled Task
```kotlin
repository.createTask(
    sessionId = currentSessionId,
    taskName = "Send reminder at 3 PM"
) { response ->
    val taskId = response.body()?.taskId
}
```

## Security Considerations

1. Always use HTTPS in production
2. Implement API key authentication
3. Add certificate pinning for sensitive endpoints
4. Sanitize user input before sending
5. Store session tokens securely
6. Implement rate limiting client-side

## Troubleshooting

### Connection Issues
- Verify backend service is running
- Check base URL configuration
- Ensure network connectivity

### Timeout Errors
- Increase timeout values if needed
- Check backend response times
- Verify network speed

### Serialization Errors
- Ensure API response matches data class structure
- Check @SerializedName annotations
- Verify JSON field names

## Additional Resources

- [Agent-S Backend Documentation](../README.md)
- [Retrofit Official Documentation](https://square.github.io/retrofit/)
- [GSON User Guide](https://github.com/google/gson/blob/master/UserGuide.md)
