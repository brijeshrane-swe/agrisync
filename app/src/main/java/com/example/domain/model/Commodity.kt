package com.example.domain.model

data class Commodity(
    val id: String,
    val commodityName: String,
    val marketCentre: String,
    val state: String,
    val variety: String = "Standard",
    val minPrice: Double,
    val maxPrice: Double,
    val modalPrice: Double,
    val priceUnit: String = "₹/Quintal",
    val arrivalDate: String,
    val priceChangePercent: Double = 0.0,
    val priceTrend: PriceTrend = PriceTrend.STABLE,
    val isSelfHealed: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class PriceTrend {
    UP,
    DOWN,
    STABLE
}

data class MarketSummary(
    val totalCommodities: Int,
    val topGainer: Commodity?,
    val topLoser: Commodity?,
    val averageMarketRate: Double,
    val activeMarketsCount: Int,
    val isLiveScraperData: Boolean
)
