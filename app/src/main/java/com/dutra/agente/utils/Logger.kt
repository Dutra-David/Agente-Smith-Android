package com.dutra.agente.utils

import timber.log.Timber

/**
 * Logger centralizado utilizando Timber
 * Fornece metodos para logging estruturado em todo o app
 */
object Logger {

    /**
     * Inicializa o Timber com comportamento apropriado para Debug/Release
     */
    fun init(isDebug: Boolean = BuildConfig.DEBUG) {
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else {
            // Em producao, plant uma tree customizada se necessario
            Timber.plant(ReleaseTree())
        }
    }

    /**
     * Log debug - informacoes para desenvolvimento
     */
    fun debug(message: String, tag: String? = null) {
        Timber.tag(tag).d(message)
    }

    /**
     * Log info - informacoes importantes
     */
    fun info(message: String, tag: String? = null) {
        Timber.tag(tag).i(message)
    }

    /**
     * Log warning - alertas e situacoes anormais
     */
    fun warning(message: String, exception: Throwable? = null, tag: String? = null) {
        if (exception != null) {
            Timber.tag(tag).w(exception, message)
        } else {
            Timber.tag(tag).w(message)
        }
    }

    /**
     * Log error - erros da aplicacao
     */
    fun error(message: String, exception: Throwable? = null, tag: String? = null) {
        if (exception != null) {
            Timber.tag(tag).e(exception, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }

    /**
     * Log api - requisicoes de API
     */
    fun api(message: String) {
        info("[API] $message", "ApiClient")
    }

    /**
     * Log repository - operacoes de repositorio
     */
    fun repository(message: String) {
        debug("[Repository] $message", "Repository")
    }

    /**
     * Log viewmodel - eventos de viewmodel
     */
    fun viewmodel(message: String) {
        debug("[ViewModel] $message", "ViewModel")
    }
}

/**
 * Tree customizada para logs em modo Release
 * Pode ser estendida para enviar logs a servicos remotos (Firebase, etc)
 */
class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Em producao, apenas logar erros e warnings
        if (priority >= android.util.Log.WARN) {
            // TODO: Enviar para servico de crash reporting (Firebase Crashlytics, etc)
        }
    }
}
