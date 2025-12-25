package com.dutra.agente.domain

/**
 * IMPROVEMENT 11: Advanced State Management
 */
class StateManagement {
    private var state = mutableMapOf<String, Any>()
    fun updateState(key: String, value: Any) { state[key] = value }
    fun getState(key: String): Any? = state[key]
}
