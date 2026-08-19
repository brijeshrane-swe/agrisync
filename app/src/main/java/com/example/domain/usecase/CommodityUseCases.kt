package com.example.domain.usecase

import com.example.domain.model.Commodity
import com.example.domain.repository.CommodityRepository
import kotlinx.coroutines.flow.Flow

class GetCommoditiesUseCase(
    private val repository: CommodityRepository
) {
    operator fun invoke(): Flow<List<Commodity>> {
        return repository.getCommoditiesStream()
    }
}

class SyncCommoditiesUseCase(
    private val repository: CommodityRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<Unit> {
        return repository.syncCommoditiesFromBrightData(forceRefresh)
    }
}

class GetAIAdvisoryUseCase(
    private val repository: CommodityRepository
) {
    suspend operator fun invoke(commodity: Commodity) = repository.getMarketAdvisory(commodity)
}

class TriggerSelfHealUseCase(
    private val repository: CommodityRepository
) {
    suspend operator fun invoke(reason: String) = repository.triggerScraperSelfHeal(reason)
}
