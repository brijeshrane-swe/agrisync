package com.example.domain.model

data class ScraperHealth(
    val collectorId: String = "c_apmc_spice_v1_09x",
    val isHealed: Boolean = true,
    val lastExecutionId: String = "j_batch_99214a",
    val status: ScraperJobStatus = ScraperJobStatus.READY,
    val healVersion: Int = 3,
    val zeroDowntimeUptimePercent: Double = 99.98,
    val lastHealPrompt: String = "Auto-healed: APMC portal migrated from <table> to dynamic <div> flexbox cards. Schema preserved.",
    val targetUrl: String = "https://www.indianspices.com/marketing/price/domestic/current-market-price.html"
)

enum class ScraperJobStatus {
    READY,
    TRIGGERING,
    EXTRACTING_DCA,
    HEALING,
    SUCCESS,
    FAILED
}

data class AIAdvisory(
    val commodityName: String,
    val marketRecommendation: String,
    val priceForecast: String,
    val strategicAction: String, // e.g. "SELL NOW", "HOLD", "PARTIAL DISPATCH"
    val confidenceScore: Double = 0.92,
    val thinkingSummary: String = ""
)
