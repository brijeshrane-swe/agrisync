package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DcaTriggerRequest(
    @Json(name = "url") val url: String,
    @Json(name = "custom_properties") val customProperties: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class DcaTriggerResponse(
    @Json(name = "collection_id") val collectionId: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "start_eta") val startEta: String?,
    @Json(name = "collector_id") val collectorId: String?
)

@JsonClass(generateAdapter = true)
data class DcaDatasetItemDto(
    @Json(name = "commodity_name") val commodityName: String?,
    @Json(name = "market_center") val marketCenter: String?,
    @Json(name = "state") val state: String?,
    @Json(name = "variety") val variety: String?,
    @Json(name = "min_price") val minPrice: Double?,
    @Json(name = "max_price") val maxPrice: Double?,
    @Json(name = "modal_price") val modalPrice: Double?,
    @Json(name = "price_unit") val priceUnit: String?,
    @Json(name = "arrival_date") val arrivalDate: String?,
    @Json(name = "price_change_percent") val priceChangePercent: Double?,
    @Json(name = "price_trend") val priceTrend: String?,
    @Json(name = "self_healed") val selfHealed: Boolean?
)

@JsonClass(generateAdapter = true)
data class SelfHealTriggerRequest(
    @Json(name = "collector_id") val collectorId: String,
    @Json(name = "prompt") val prompt: String,
    @Json(name = "auto_approve") val autoApprove: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SelfHealTriggerResponse(
    @Json(name = "status") val status: String, // "awaiting_approval", "success"
    @Json(name = "collector_id") val collectorId: String,
    @Json(name = "preview_result") val previewResult: List<DcaDatasetItemDto>?,
    @Json(name = "message") val message: String?
)
