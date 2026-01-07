package com.galang.bbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.ui.components.ActivityTimelineItem
import com.galang.bbooks.ui.components.AvatarCircle
import com.galang.bbooks.ui.components.LiquidGlassCard
import com.galang.bbooks.ui.components.LiquidGlassSummaryCard
import com.galang.bbooks.ui.theme.*
import com.galang.bbooks.ui.viewmodel.DashboardViewModel
import com.galang.bbooks.ui.viewmodel.DashboardViewModelFactory

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(app.bookRepository, app.transactionRepository)
    )
    val user = app.userRepository.currentUser

    val totalBooks by viewModel.totalBooks.collectAsState()
    val availableBooks by viewModel.availableBooks.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Decorative background circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 200.dp, y = (-80).dp)
                .clip(CircleShape)
                .background(PurpleAccent.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 400.dp)
                .clip(CircleShape)
                .background(BlueAccent.copy(alpha = 0.06f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header with greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Hello,",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = user?.fullName ?: "Reader!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                
                AvatarCircle(
                    initial = user?.username?.firstOrNull()?.toString() ?: "U",
                    size = 50.dp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Overview Section
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LiquidGlassSummaryCard(
                    title = "Total Buku",
                    count = totalBooks.toString(),
                    icon = Icons.Default.LibraryBooks,
                    accentColor = PurpleAccent,
                    modifier = Modifier.weight(1f)
                )
                LiquidGlassSummaryCard(
                    title = "Tersedia",
                    count = availableBooks.toString(),
                    icon = Icons.Default.CheckCircle,
                    accentColor = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Aktivitas Terbaru Section
            Text(
                text = "Aktivitas Terbaru",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    ActivityTimelineItem(
                        title = "Pinjam: \"The Midnight Library\"",
                        subtitle = "2 jam yang lalu",
                        icon = Icons.Default.MenuBook,
                        iconColor = PurpleAccent
                    )
                    ActivityTimelineItem(
                        title = "Kembali: \"Sapiens\"",
                        subtitle = "Kemarin",
                        icon = Icons.Default.Refresh,
                        iconColor = StatusGreen
                    )
                    ActivityTimelineItem(
                        title = "Pinjam: \"Dune\"",
                        subtitle = "3 hari yang lalu",
                        icon = Icons.Default.MenuBook,
                        iconColor = PurpleAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buku Populer Section
            Text(
                text = "Buku Populer",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PopularBookCard(title = "Atomic Habits", author = "James Clear")
                PopularBookCard(title = "Project Hail Mary", author = "Andy Weir")
                PopularBookCard(title = "Educated", author = "Tara Westover")
                PopularBookCard(title = "1984", author = "George Orwell")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Motivational Banner
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    PurpleAccent.copy(alpha = 0.3f),
                                    BlueAccent.copy(alpha = 0.3f)
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = "Discover New Worlds",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Check out our latest collection!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Bottom spacing for nav bar
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PopularBookCard(
    title: String,
    author: String
) {
    LiquidGlassCard(
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Book cover placeholder
            Box(
                modifier = Modifier
                    .size(100.dp, 140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PurpleAccent, BlueAccent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
