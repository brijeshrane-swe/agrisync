package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Commodity
import com.example.domain.model.PriceTrend
import com.example.ui.theme.BentoBorderOutline
import com.example.ui.theme.BentoLiveGreen
import com.example.ui.theme.BentoPurpleContainer
import com.example.ui.theme.BentoPurpleDark
import com.example.ui.theme.BentoPurpleLight
import com.example.ui.theme.BentoPurpleOnContainer
import com.example.ui.theme.BentoPurplePrimary
import com.example.ui.theme.BentoRoseContainer
import com.example.ui.theme.BentoRoseOnContainer
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.PriceDownRed
import com.example.ui.theme.PriceUpGreen

@Composable
fun CommodityCard(
    commodity: Commodity,
    onGetAdvisory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("commodity_card_${commodity.id}")
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, BentoBorderOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Commodity Name & Bento Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = commodity.commodityName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = commodity.variety,
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }

                if (commodity.isSelfHealed) {
                    Surface(
                        color = BentoPurpleContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Self Healed",
                                tint = BentoPurplePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Self-Healed",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BentoPurpleOnContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bento Sub-Grid: Price Block & Trend Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Modal Rate Bento Box
                Surface(
                    modifier = Modifier.weight(1.3f),
                    color = BentoSurfaceElevated,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, BentoBorderOutline.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "MODAL RATE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = BentoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "₹${formatCurrency(commodity.modalPrice)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                ),
                                color = BentoPurpleDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = commodity.priceUnit,
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                // Trend Pill Bento Box
                BentoTrendTile(
                    trend = commodity.priceTrend,
                    changePercent = commodity.priceChangePercent,
                    modifier = Modifier.weight(0.9f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Market Strip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BentoSurfaceElevated.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Market location",
                            tint = BentoPurplePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${commodity.marketCentre}, ${commodity.state}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = BentoTextPrimary,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "Min ₹${formatCurrency(commodity.minPrice)} • Max ₹${formatCurrency(commodity.maxPrice)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Advisor Button (Deep Bento Purple)
            Button(
                onClick = onGetAdvisory,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("get_ai_advisory_${commodity.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoPurpleDark,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Advisor",
                    modifier = Modifier.size(16.dp),
                    tint = BentoPurpleLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Get Gemini Farmer Advisory",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun BentoTrendTile(
    trend: PriceTrend,
    changePercent: Double,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon, label) = when (trend) {
        PriceTrend.UP -> TrendQuad(
            PriceUpGreen.copy(alpha = 0.12f),
            PriceUpGreen,
            Icons.Default.TrendingUp,
            "+${changePercent}%"
        )
        PriceTrend.DOWN -> TrendQuad(
            BentoRoseContainer,
            BentoRoseOnContainer,
            Icons.Default.TrendingDown,
            "${changePercent}%"
        )
        PriceTrend.STABLE -> TrendQuad(
            BentoSurfaceElevated,
            BentoTextSecondary,
            Icons.Default.TrendingFlat,
            "0.0%"
        )
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BentoBorderOutline.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "24H TREND",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                color = BentoTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Trend",
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    ),
                    color = textColor
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return String.format("%,.0f", amount)
}

private data class TrendQuad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
