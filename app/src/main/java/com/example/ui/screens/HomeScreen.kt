package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Commodity
import com.example.domain.model.MarketSummary
import com.example.domain.model.ScraperHealth
import com.example.ui.components.AIAdvisoryDialog
import com.example.ui.components.CommodityCard
import com.example.ui.components.ConfigDialog
import com.example.ui.components.ScraperTelemetrySheet
import com.example.ui.theme.BentoBorderOutline
import com.example.ui.theme.BentoBorderStroke
import com.example.ui.theme.BentoCanvasBackground
import com.example.ui.theme.BentoLiveGreen
import com.example.ui.theme.BentoPurpleContainer
import com.example.ui.theme.BentoPurpleDark
import com.example.ui.theme.BentoPurpleLight
import com.example.ui.theme.BentoPurpleOnContainer
import com.example.ui.theme.BentoPurplePrimary
import com.example.ui.theme.BentoRoseContainer
import com.example.ui.theme.BentoRoseOnContainer
import com.example.ui.theme.BentoSurfaceCard
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.viewmodel.AgriSyncUiState
import com.example.ui.viewmodel.AgriSyncViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AgriSyncViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedState by viewModel.selectedState.collectAsStateWithLifecycle()
    val selectedSpice by viewModel.selectedSpice.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val scraperHealth by viewModel.scraperHealth.collectAsStateWithLifecycle()
    val aiAdvisoryState by viewModel.aiAdvisoryState.collectAsStateWithLifecycle()
    val selectedCommodityForAdvisory by viewModel.selectedCommodityForAdvisory.collectAsStateWithLifecycle()
    val showTelemetrySheet by viewModel.showTelemetrySheet.collectAsStateWithLifecycle()
    val showConfigDialog by viewModel.showConfigDialog.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("home") }

    val statesList = listOf("All", "Karnataka", "Kerala", "Tamil Nadu", "Andhra Pradesh", "Gujarat")
    val spicesList = listOf("All", "Pepper", "Cardamom", "Turmeric", "Coriander", "Ginger", "Chilli", "Cumin")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        containerColor = BentoCanvasBackground,
        bottomBar = {
            BentoBottomNavigation(
                activeTab = activeTab,
                onTabSelected = { tab ->
                    activeTab = tab
                    when (tab) {
                        "telemetry" -> viewModel.toggleTelemetrySheet(true)
                        "config" -> viewModel.toggleConfigDialog(true)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BentoCanvasBackground)
        ) {
            // Bento Header
            BentoHeader(
                onOpenTelemetry = { viewModel.toggleTelemetrySheet(true) },
                onOpenConfig = { viewModel.toggleConfigDialog(true) }
            )

            when (val state = uiState) {
                is AgriSyncUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = BentoPurplePrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading Bento Market Dashboard...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }

                is AgriSyncUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is AgriSyncUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Bento Grid Master Dashboard
                        item {
                            BentoDashboardGrid(
                                scraperHealth = scraperHealth,
                                summary = state.summary,
                                isSyncing = isSyncing,
                                lastSyncTimestamp = state.lastSyncTimestamp,
                                onSync = { viewModel.syncData() },
                                onOpenTelemetry = { viewModel.toggleTelemetrySheet(true) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // Bento Search Field
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.onSearchQueryChanged(it) },
                                placeholder = {
                                    Text(
                                        "Search commodity, market yard, or state...",
                                        color = BentoTextMuted
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = BentoPurplePrimary
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                tint = BentoTextSecondary
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoSurfaceCard,
                                    unfocusedContainerColor = BentoSurfaceCard,
                                    focusedBorderColor = BentoPurplePrimary,
                                    unfocusedBorderColor = BentoBorderOutline
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .testTag("search_text_field")
                            )
                        }

                        // State Bento Filter Chips
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                statesList.forEach { stateName ->
                                    FilterChip(
                                        selected = selectedState == stateName,
                                        onClick = { viewModel.onStateSelected(stateName) },
                                        label = {
                                            Text(
                                                text = stateName,
                                                fontWeight = if (selectedState == stateName) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BentoPurpleContainer,
                                            selectedLabelColor = BentoPurpleOnContainer,
                                            containerColor = BentoSurfaceElevated,
                                            labelColor = BentoTextSecondary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selectedState == stateName,
                                            borderColor = if (selectedState == stateName) BentoPurplePrimary else BentoBorderOutline
                                        ),
                                        modifier = Modifier.testTag("filter_state_$stateName")
                                    )
                                }
                            }
                        }

                        // Spice Category Filter Chips
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                spicesList.forEach { spice ->
                                    FilterChip(
                                        selected = selectedSpice == spice,
                                        onClick = { viewModel.onSpiceSelected(spice) },
                                        label = {
                                            Text(
                                                text = spice,
                                                fontWeight = if (selectedSpice == spice) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BentoRoseContainer,
                                            selectedLabelColor = BentoRoseOnContainer,
                                            containerColor = BentoSurfaceElevated,
                                            labelColor = BentoTextSecondary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selectedSpice == spice,
                                            borderColor = if (selectedSpice == spice) BentoRoseOnContainer else BentoBorderOutline
                                        ),
                                        modifier = Modifier.testTag("filter_spice_$spice")
                                    )
                                }
                            }
                        }

                        // Bento Section Header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Commodity Board (${state.filteredCommodities.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = BentoTextPrimary
                                )
                                Text(
                                    text = "Sorted by Modal Rate",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoTextSecondary
                                )
                            }
                        }

                        // Commodity Cards List
                        if (state.filteredCommodities.isEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = BentoSurfaceElevated,
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, BentoBorderOutline)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No commodities found matching filter criteria.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = BentoTextSecondary
                                        )
                                    }
                                }
                            }
                        } else {
                            items(
                                items = state.filteredCommodities,
                                key = { it.id }
                            ) { commodity ->
                                CommodityCard(
                                    commodity = commodity,
                                    onGetAdvisory = { viewModel.requestAIAdvisory(commodity) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // AI Advisory Modal Dialog
        selectedCommodityForAdvisory?.let { commodity ->
            AIAdvisoryDialog(
                commodity = commodity,
                state = aiAdvisoryState,
                onDismiss = { viewModel.dismissAIAdvisory() }
            )
        }

        // Scraper Telemetry Bottom Sheet
        if (showTelemetrySheet) {
            ScraperTelemetrySheet(
                scraperHealth = scraperHealth,
                onDismiss = { viewModel.toggleTelemetrySheet(false) },
                onTriggerSelfHeal = { reason -> viewModel.triggerSelfHeal(reason) }
            )
        }

        // Config Dialog
        if (showConfigDialog) {
            ConfigDialog(
                scraperHealth = scraperHealth,
                onDismiss = { viewModel.toggleConfigDialog(false) }
            )
        }
    }
}

@Composable
fun BentoHeader(
    onOpenTelemetry: () -> Unit,
    onOpenConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "SCRAPEVERSE HACKATHON",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = BentoPurplePrimary
            )
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = BentoTextPrimary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onOpenTelemetry,
                modifier = Modifier
                    .size(40.dp)
                    .background(BentoSurfaceElevated, shape = CircleShape)
                    .testTag("open_telemetry_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = "Telemetry",
                    tint = BentoPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onOpenConfig,
                modifier = Modifier
                    .size(40.dp)
                    .background(BentoSurfaceElevated, shape = CircleShape)
                    .testTag("open_config_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Config",
                    tint = BentoPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Bento Avatar Pill
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = BentoPurpleLight
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "SV",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = BentoPurpleDark
                    )
                }
            }
        }
    }
}

@Composable
fun BentoDashboardGrid(
    scraperHealth: ScraperHealth,
    summary: MarketSummary,
    isSyncing: Boolean,
    lastSyncTimestamp: Long,
    onSync: () -> Unit,
    onOpenTelemetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tile 1: Hero Active Node Bento Tile (2-column span)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = BentoSurfaceElevated,
            border = BorderStroke(1.dp, BentoBorderOutline)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header of Tile 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = BentoPurplePrimary,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "ACTIVE NODE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onSync,
                        enabled = !isSyncing,
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = BentoPurplePrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync",
                                tint = BentoTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ScraperStudio Instance",
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary
                )

                Text(
                    text = scraperHealth.collectorId,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 22.sp
                    ),
                    color = BentoTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BentoLiveGreen)
                    )
                    Text(
                        text = "Syncing with Room & DCA Pipeline (v${scraperHealth.healVersion})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = BentoTextSecondary
                    )
                }
            }
        }

        // Tile 2 & 3: Twin Bento Split Tiles (Rose + Lavender)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tile 2: Soft Rose Deadline Container
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(26.dp),
                color = BentoRoseContainer
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = BentoRoseOnContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = "Deadline",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column {
                        Text(
                            text = "DEADLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = BentoRoseOnContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Aug 23",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = BentoRoseOnContainer
                        )
                        Text(
                            text = "5 days remaining",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoRoseOnContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Tile 3: Soft Lavender Market Metrics Container
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(26.dp),
                color = BentoPurpleContainer
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = BentoPurpleDark
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "APMC Markets",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column {
                        Text(
                            text = "APMC YARDS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = BentoPurpleDark.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${summary.activeMarketsCount} Active",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = BentoPurpleDark
                        )
                        Text(
                            text = "${summary.totalCommodities} Tracked • 100% Uptime",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoPurpleDark.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Tile 4: Compose UI & Telemetry Preview Tile (White card with border)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenTelemetry() },
            shape = RoundedCornerShape(24.dp),
            color = BentoSurfaceCard,
            border = BorderStroke(1.dp, BentoBorderStroke)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = BentoSurfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = "Telemetry",
                                tint = BentoPurplePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Scraper Studio Telemetry",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "bdata scraper heal • Zero Schema Breaks",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Open",
                    tint = BentoPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Tile 5: Dark Indigo Bento Shell CTA Tile
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSync() }
                .testTag("bento_sync_cta_tile"),
            shape = RoundedCornerShape(24.dp),
            color = BentoPurpleDark
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Triggering DCA Batch Pipeline...",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Shell",
                        tint = BentoPurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open ScraperStudio Shell & Live DCA Sync",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BentoBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BentoSurfaceElevated,
        border = BorderStroke(1.dp, BentoBorderOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BentoNavItem(
                icon = Icons.Default.Storefront,
                label = "Home",
                isActive = activeTab == "home",
                onClick = { onTabSelected("home") }
            )

            BentoNavItem(
                icon = Icons.Default.Hub,
                label = "Telemetry",
                isActive = activeTab == "telemetry",
                onClick = { onTabSelected("telemetry") }
            )

            BentoNavItem(
                icon = Icons.Default.Tune,
                label = "Config",
                isActive = activeTab == "config",
                onClick = { onTabSelected("config") }
            )
        }
    }
}

@Composable
fun BentoNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = if (isActive) BentoPurpleContainer else Color.Transparent,
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) BentoTextPrimary else BentoTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = if (isActive) BentoTextPrimary else BentoTextSecondary
        )
    }
}
