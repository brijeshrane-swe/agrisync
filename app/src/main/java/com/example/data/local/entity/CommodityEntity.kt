package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Commodity
import com.example.domain.model.PriceTrend

@Entity(tableName = "commodities")
data class CommodityEntity(
    @PrimaryKey
    val id: String,
    val commodityName: String,
    val marketCentre: String,
    val state: String,
    val variety: String,
    val minPrice: Double,
    val maxPrice: Double,
    val modalPrice: Double,
    val priceUnit: String,
    val arrivalDate: String,
    val priceChangePercent: Double,
    val priceTrend: String, // "UP", "DOWN", "STABLE"
    val isSelfHealed: Boolean,
    val lastUpdated: Long
) {
    fun toDomain(): Commodity {
        val trend = try {
            PriceTrend.valueOf(priceTrend)
        } catch (_: Exception) {
            PriceTrend.STABLE
        }
        return Commodity(
            id = id,
            commodityName = commodityName,
            marketCentre = marketCentre,
            state = state,
            variety = variety,
            minPrice = minPrice,
            maxPrice = maxPrice,
            modalPrice = modalPrice,
            priceUnit = priceUnit,
            arrivalDate = arrivalDate,
            priceChangePercent = priceChangePercent,
            priceTrend = trend,
            isSelfHealed = isSelfHealed,
            lastUpdated = lastUpdated
        )
    }

    companion object {
        fun fromDomain(model: Commodity): CommodityEntity {
            return CommodityEntity(
                id = model.id,
                commodityName = model.commodityName,
                marketCentre = model.marketCentre,
                state = model.state,
                variety = model.variety,
                minPrice = model.minPrice,
                maxPrice = model.maxPrice,
                modalPrice = model.modalPrice,
                priceUnit = model.priceUnit,
                arrivalDate = model.arrivalDate,
                priceChangePercent = model.priceChangePercent,
                priceTrend = model.priceTrend.name,
                isSelfHealed = model.isSelfHealed,
                lastUpdated = model.lastUpdated
            )
        }
    }
}
