package com.dutra.agente.utils

/**
 * Sealed class para tratamento de resultado de operacoes assincronas
 * Permite diferenciar entre sucesso e falha de forma type-safe
 */
sealed class Result<out T> {
    /**
     * Sucesso com dados
     * @param data Dados retornados pela operacao
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Erro com mensagem
     * @param message Mensagem de erro
     * @param exception Excecao original (opcional)
     */
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()

    /**
     * Estado de carregamento
     */
    object Loading : Result<Nothing>()

    // ============= Extension Functions =============

    /**
     * Mapeia o valor de sucesso para outro tipo
     */
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(this.data))
            is Error -> Error(this.message, this.exception)
            is Loading -> Loading
        }
    }

    /**
     * Executa uma funcao se o resultado for sucesso
     */
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> {
        return when (this) {
            is Success -> transform(this.data)
            is Error -> Error(this.message, this.exception)
            is Loading -> Loading
        }
    }

    /**
     * Executa uma acao se o resultado for sucesso (sem transformar)
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) {
            action(this.data)
        }
        return this
    }

    /**
     * Executa uma acao se o resultado for erro
     */
    inline fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) {
            action(this)
        }
        return this
    }

    /**
     * Retorna o valor ou null se for erro
     */
    fun getOrNull(): T? {
        return when (this) {
            is Success -> this.data
            else -> null
        }
    }

    /**
     * Retorna o valor ou valor padrao se for erro
     */
    fun getOrElse(default: T): T {
        return when (this) {
            is Success -> this.data
            else -> default
        }
    }

    /**
     * Verifica se eh sucesso
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Verifica se eh erro
     */
    fun isError(): Boolean = this is Error

    /**
     * Verifica se eh carregamento
     */
    fun isLoading(): Boolean = this is Loading
}
