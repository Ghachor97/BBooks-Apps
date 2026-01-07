package com.galang.bbooks.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.ui.theme.DeepBlue
import com.galang.bbooks.ui.theme.PastelBlue
import com.galang.bbooks.ui.theme.PastelMint
import com.galang.bbooks.ui.theme.SoftBlue
import com.galang.bbooks.ui.theme.SoftCoral
import com.galang.bbooks.ui.viewmodel.DashboardViewModel
import com.galang.bbooks.ui.viewmodel.DashboardViewModelFactory

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(app.bookRepository, app.transactionRepository)
    )
    val user = app.userRepository.currentUser // Get current user for greeting

    val totalBooks by viewModel.totalBooks.collectAsState()
    val availableBooks by viewModel.availableBooks.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PastelBlue)
        ) {
            // Background Deco
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 200.dp, y = (-100).dp)
                    .clip(CircleShape)
                    .background(SoftBlue.copy(alpha = 0.1f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Hello,",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepBlue.copy(alpha = 0.7f)
                        )
                        Text(
                            text = user?.fullName ?: "Reader!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepBlue
                        )
                    }
                    // Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(SoftCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.username?.firstOrNull()?.uppercase() ?: "U",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Summary Cards
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = DeepBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernSummaryCard(
                        title = "Total Books",
                        count = totalBooks.toString(),
                        icon = Icons.Default.Book,
                        color = SoftBlue,
                        modifier = Modifier.weight(1f)
                    )
                    ModernSummaryCard(
                        title = "Available",
                        count = availableBooks.toString(),
                        icon = Icons.Default.Schedule, // Should find a better one like EventAvailable if possible, but Schedule works for now
                        color = SoftCoral,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Popular Section Placeholder
                 Text(
                    text = "Why Read Today?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = DeepBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Card(
                     modifier = Modifier.fillMaxWidth().height(150.dp),
                     shape = RoundedCornerShape(24.dp),
                     colors = CardDefaults.cardColors(containerColor = PastelMint)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.CenterStart) {
                         Column {
                            Text(
                                text = "Discover New Worlds",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = DeepBlue
                            )
                             Text(
                                text = "Check out our latest collection!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeepBlue.copy(alpha = 0.8f)
                            )
                         }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernSummaryCard(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f) // Slightly more opaque
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Remove shadow "stroke"
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)) // Very subtle border definition
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)), // More subtle icon background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = count,
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold), // Reduced weight
                    color = DeepBlue
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepBlue.copy(alpha = 0.6f)
                )
            }
        }
    }
}
