package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AIAdvisory
import com.example.domain.model.Commodity
import com.example.domain.model.MarketSummary
import com.example.domain.model.PriceTrend
import com.example.domain.model.ScraperHealth
import com.example.domain.model.ScraperJobStatus
import com.example.domain.repository.CommodityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AgriSyncUiState {
    object Loading : AgriSyncUiState
    data class Success(
        val commodities: List<Commodity>,
        val filteredCommodities: List<Commodity>,
        val summary: MarketSummary,
        val isOffline: Boolean,
        val lastSyncTimestamp: Long
    ) : AgriSyncUiState
    data class Error(val message: String) : AgriSyncUiState
}

sealed interface AIAdvisoryUiState {
    object Idle : AIAdvisoryUiState
    object Loading : AIAdvisoryUiState
    data class Success(val advisory: AIAdvisory) : AIAdvisoryUiState
    data class Error(val message: String) : AIAdvisoryUiState
}

class AgriSyncViewModel(
    private val repository: CommodityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedState = MutableStateFlow("All")
    val selectedState: StateFlow<String> = _selectedState.asStateFlow()

    private val _selectedSpice = MutableStateFlow("All")
    val selectedSpice: StateFlow<String> = _selectedSpice.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _aiAdvisoryState = MutableStateFlow<AIAdvisoryUiState>(AIAdvisoryUiState.Idle)
    val aiAdvisoryState: StateFlow<AIAdvisoryUiState> = _aiAdvisoryState.asStateFlow()

    private val _selectedCommodityForAdvisory = MutableStateFlow<Commodity?>(null)
    val selectedCommodityForAdvisory: StateFlow<Commodity?> = _selectedCommodityForAdvisory.asStateFlow()

    private val _showTelemetrySheet = MutableStateFlow(false)
    val showTelemetrySheet: StateFlow<Boolean> = _showTelemetrySheet.asStateFlow()

    private val _showConfigDialog = MutableStateFlow(false)
    val showConfigDialog: StateFlow<Boolean> = _showConfigDialog.asStateFlow()

    val scraperHealth: StateFlow<ScraperHealth> = repository.getScraperHealthStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScraperHealth()
        )

    val uiState: StateFlow<AgriSyncUiState> = combine(
        repository.getCommoditiesStream(),
        _searchQuery,
        _selectedState,
        _selectedSpice,
        _isSyncing
    ) { commodities, query, stateFilter, spiceFilter, syncing ->
        if (commodities.isEmpty() && !syncing) {
            AgriSyncUiState.Loading
        } else {
            val filtered = commodities.filter { item ->
                val matchesQuery = query.isBlank() ||
                        item.commodityName.contains(query, ignoreCase = true) ||
                        item.marketCentre.contains(query, ignoreCase = true) ||
                        item.state.contains(query, ignoreCase = true)

                val matchesState = stateFilter == "All" || item.state.equals(stateFilter, ignoreCase = true)

                val matchesSpice = spiceFilter == "All" || item.commodityName.contains(spiceFilter, ignoreCase = true)

                matchesQuery && matchesState && matchesSpice
            }

            val topGainer = commodities.maxByOrNull { it.priceChangePercent }
            val topLoser = commodities.minByOrNull { it.priceChangePercent }
            val avgRate = if (commodities.isNotEmpty()) commodities.map { it.modalPrice }.average() else 0.0
            val activeMarkets = commodities.map { it.marketCentre }.distinct().size

            val summary = MarketSummary(
                totalCommodities = commodities.size,
                topGainer = topGainer,
                topLoser = topLoser,
                averageMarketRate = avgRate,
                activeMarketsCount = activeMarkets,
                isLiveScraperData = true
            )

            val latestTimestamp = commodities.maxOfOrNull { it.lastUpdated } ?: System.currentTimeMillis()

            AgriSyncUiState.Success(
                commodities = commodities,
                filteredCommodities = filtered,
                summary = summary,
                isOffline = false,
                lastSyncTimestamp = latestTimestamp
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AgriSyncUiState.Loading
    )

    init {
        // Initial sync check
        viewModelScope.launch {
            repository.syncCommoditiesFromBrightData(forceRefresh = false)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStateSelected(state: String) {
        _selectedState.value = state
    }

    fun onSpiceSelected(spice: String) {
        _selectedSpice.value = spice
    }

    fun syncData() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncCommoditiesFromBrightData(forceRefresh = true)
            _isSyncing.value = false
        }
    }

    fun requestAIAdvisory(commodity: Commodity) {
        _selectedCommodityForAdvisory.value = commodity
        _aiAdvisoryState.value = AIAdvisoryUiState.Loading
        viewModelScope.launch {
            val result = repository.getMarketAdvisory(commodity)
            result.onSuccess { advisory ->
                _aiAdvisoryState.value = AIAdvisoryUiState.Success(advisory)
            }.onFailure { error ->
                _aiAdvisoryState.value = AIAdvisoryUiState.Error(error.message ?: "Failed to generate AI advisory")
            }
        }
    }

    fun dismissAIAdvisory() {
        _selectedCommodityForAdvisory.value = null
        _aiAdvisoryState.value = AIAdvisoryUiState.Idle
    }

    fun triggerSelfHeal(reason: String = "APMC portal updated HTML table structure") {
        viewModelScope.launch {
            repository.triggerScraperSelfHeal(reason)
        }
    }

    fun toggleTelemetrySheet(show: Boolean) {
        _showTelemetrySheet.value = show
    }

    fun toggleConfigDialog(show: Boolean) {
        _showConfigDialog.value = show
    }

    companion object {
        fun provideFactory(repository: CommodityRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AgriSyncViewModel(repository) as T
                }
            }
    }
}
