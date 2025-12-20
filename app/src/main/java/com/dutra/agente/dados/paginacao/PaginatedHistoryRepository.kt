package com.dutra.agente.dados.paginacao

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class PaginationParams(
    val pageSize: Int = 20,
    val enablePlaceholders: Boolean = false,
    val maxSize: Int = 200
)

data class PagedItem<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean
)

interface IPaginatedRepository<T> {
    fun getPagedData(pageSize: Int = 20): Flow<PagingData<T>>
    suspend fun getPage(pageNumber: Int, pageSize: Int = 20): PagedItem<T>
    suspend fun refresh()
}

class PaginatedHistoryRepository @Inject constructor(
    private val historyDao: Any // Será injetado quando houver DAO
) : IPaginatedRepository<String> {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        private const val DEFAULT_MAX_SIZE = 200
        private const val ENABLE_PLACEHOLDERS = false
    }

    private var currentPage = 0
    private val pagingConfig = PagingConfig(
        pageSize = DEFAULT_PAGE_SIZE,
        enablePlaceholders = ENABLE_PLACEHOLDERS,
        maxSize = DEFAULT_MAX_SIZE
    )

    /**
     * Retorna um Flow de dados paginados
     * Compatível com RecyclerView + PagingAdapter
     */
    override fun getPagedData(pageSize: Int): Flow<PagingData<String>> {
        val config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = ENABLE_PLACEHOLDERS,
            maxSize = pageSize * 10
        )
        
        return Pager(
            config = config,
            pagingSourceFactory = {
                object : androidx.paging.PagingSource<Int, String>() {
                    override suspend fun load(
                        params: LoadParams<Int>
                    ): LoadResult<Int, String> {
                        return try {
                            val pageNumber = params.key ?: 0
                            val items = mutableListOf<String>()
                            
                            // Simular dados paginados
                            for (i in 0 until pageSize) {
                                items.add("Item ${pageNumber * pageSize + i}")
                            }
                            
                            LoadResult.Page(
                                data = items,
                                prevKey = if (pageNumber > 0) pageNumber - 1 else null,
                                nextKey = pageNumber + 1
                            )
                        } catch (e: Exception) {
                            LoadResult.Error(e)
                        }
                    }

                    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
                        return state.anchorPosition?.let { anchorPosition ->
                            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                        }
                    }
                }
            }
        ).flow
    }

    /**
     * Recupera uma página específica de dados
     * Ütil para scroll automático ou filtros
     */
    override suspend fun getPage(
        pageNumber: Int,
        pageSize: Int
    ): PagedItem<String> {
        return try {
            val items = mutableListOf<String>()
            val startIndex = pageNumber * pageSize
            val totalItems = 500 // Valor simulado
            val totalPages = (totalItems + pageSize - 1) / pageSize
            
            for (i in 0 until pageSize) {
                if (startIndex + i < totalItems) {
                    items.add("Item ${startIndex + i}")
                }
            }
            
            PagedItem(
                items = items,
                currentPage = pageNumber,
                totalPages = totalPages,
                hasNextPage = pageNumber < totalPages - 1
            )
        } catch (e: Exception) {
            PagedItem(
                items = emptyList(),
                currentPage = pageNumber,
                totalPages = 0,
                hasNextPage = false
            )
        }
    }

    /**
     * Atualiza dados paginados (útil para pull-to-refresh)
     */
    override suspend fun refresh() {
        currentPage = 0
    }

    /**
     * Calcula número total de páginas
     */
    fun getTotalPages(itemCount: Int, pageSize: Int = DEFAULT_PAGE_SIZE): Int {
        return (itemCount + pageSize - 1) / pageSize
    }

    /**
     * Valida se página existe
     */
    fun isValidPage(pageNumber: Int, totalPages: Int): Boolean {
        return pageNumber >= 0 && pageNumber < totalPages
    }
}
