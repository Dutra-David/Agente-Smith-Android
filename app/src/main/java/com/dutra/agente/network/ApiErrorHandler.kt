package com.dutra.agente.network

import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ApiErrorHandler - Mapeia excecoes HTTP para mensagens de usuario
 *
 * Responsabilidades:
 * - Converter IOException em mensagens legíveis
 * - Diferenciar erros de rede vs servidor
 * - Fornecer mensagens em portugues para UI
 * - Logar erros estruturados
 */
object ApiErrorHandler {

    /**
     * Converte excecao em mensagem legível para usuario
     */
    fun handleException(exception: Throwable): String {
        return when (exception) {
            is SocketTimeoutException -> {
                Timber.w("Timeout na requisicao")
                "Requisicao expirou. Verifique sua conexao e tente novamente."
            }
            is UnknownHostException -> {
                Timber.w("Host desconhecido")
                "Nao foi possivel conectar ao servidor. Verifique sua conexao de internet."
            }
            is IOException -> {
                Timber.w(exception, "Erro de conexao")
                "Erro de conexao. Verifique sua internet e tente novamente."
            }
            else -> {
                Timber.e(exception, "Erro desconhecido")
                "Ocorreu um erro desconhecido. Tente novamente."
            }
        }
    }

    /**
     * Trata erro HTTP baseado no codigo de status
     */
    fun handleHttpError(statusCode: Int): String {
        return when (statusCode) {
            400 -> {
                Timber.w("Erro 400: Bad Request")
                "Requisicao invalida. Verifique os dados enviados."
            }
            401 -> {
                Timber.w("Erro 401: Unauthorized")
                "Autenticacao necessaria. Por favor, faça login novamente."
            }
            403 -> {
                Timber.w("Erro 403: Forbidden")
                "Voce nao tem permissao para acessar este recurso."
            }
            404 -> {
                Timber.w("Erro 404: Not Found")
                "Recurso nao encontrado. Tente novamente mais tarde."
            }
            408 -> {
                Timber.w("Erro 408: Request Timeout")
                "Requisicao expirou. Tente novamente."
            }
            429 -> {
                Timber.w("Erro 429: Too Many Requests")
                "Muitas requisicoes. Aguarde um momento e tente novamente."
            }
            500 -> {
                Timber.e("Erro 500: Internal Server Error")
                "Erro no servidor. Tente novamente em alguns momentos."
            }
            502 -> {
                Timber.e("Erro 502: Bad Gateway")
                "Servidor indisponivel. Tente novamente mais tarde."
            }
            503 -> {
                Timber.e("Erro 503: Service Unavailable")
                "Servico temporariamente indisponivel. Tente novamente em alguns momentos."
            }
            else -> {
                Timber.e("Erro HTTP: $statusCode")
                "Erro ao processar requisicao. Codigo: $statusCode"
            }
        }
    }

    /**
     * Loga informacoes de erro para debug
     */
    fun logError(tag: String, message: String, exception: Throwable? = null) {
        if (exception != null) {
            Timber.tag(tag).e(exception, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
}
