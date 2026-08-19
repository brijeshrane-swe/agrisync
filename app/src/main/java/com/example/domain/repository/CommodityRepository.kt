package com.example.domain.repository

import com.example.domain.model.AIAdvisory
import com.example.domain.model.Commodity
import com.example.domain.model.ScraperHealth
import kotlinx.coroutines.flow.Flow

interface CommodityRepository {
    fun getCommoditiesStream(): Flow<List<Commodity>>
    suspend fun getCommodityById(id: String): Commodity?
    suspend fun syncCommoditiesFromBrightData(forceRefresh: Boolean = false): Result<Unit>
    suspend fun getMarketAdvisory(commodity: Commodity): Result<AIAdvisory>
    fun getScraperHealthStream(): Flow<ScraperHealth>
    suspend fun triggerScraperSelfHeal(reason: String): Result<ScraperHealth>
}
