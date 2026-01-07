package com.galang.bbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.galang.bbooks.ui.components.DeadlineProgressBar
import com.galang.bbooks.ui.components.LiquidGlassBookCard
import com.galang.bbooks.ui.components.LiquidGlassButton
import com.galang.bbooks.ui.components.LiquidGlassCard
import com.galang.bbooks.ui.components.LiquidGlassSummaryCard
import com.galang.bbooks.ui.components.StatusBadge
import com.galang.bbooks.ui.theme.*
import com.galang.bbooks.ui.viewmodel.BorrowState
import com.galang.bbooks.ui.viewmodel.ReturnBookViewModel
import com.galang.bbooks.ui.viewmodel.ReturnBookViewModelFactory
import com.galang.bbooks.ui.viewmodel.TransactionWithBook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ReturnBookScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: ReturnBookViewModel = viewModel(
        factory = ReturnBookViewModelFactory(
            app.transactionRepository,
            app.bookRepository,
            app.userRepository
        )
    )

    val transactions by viewModel.activeTransactions.collectAsState()
    val borrowState by viewModel.returnState.collectAsState()
    val user = app.userRepository.currentUser
    val isAdmin = user?.role == "admin"
    
    var showConditionDialog by remember { mutableStateOf<TransactionWithBook?>(null) }

    // Calculate summary stats
    val onTimeCount = transactions.count { it.fineEstimate <= 0 }
    val overdueCount = transactions.count { it.fineEstimate > 0 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Decorative background
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 180.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(PurpleAccent.copy(alpha = 0.07f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-70).dp, y = 500.dp)
                .clip(CircleShape)
                .background(BlueAccent.copy(alpha = 0.05f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Text(
                text = "Pengembalian Buku",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Summary Card
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Circular indicator
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        PurpleAccent,
                                        BlueAccent,
                                        PurpleAccent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = transactions.size.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Buku",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Stats
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Tepat Waktu",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = onTimeCount.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = StatusRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Terlambat",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = overdueCount.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusRed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Book List
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada buku yang perlu dikembalikan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(transactions) { item ->
                        ReturnBookCard(
                            item = item,
                            isAdmin = isAdmin,
                            onReturnClick = {
                                if (isAdmin) {
                                    showConditionDialog = item
                                } else {
                                    viewModel.returnBook(item)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Condition Dialog for Admin
    if (showConditionDialog != null) {
        ReturnConditionDialog(
            item = showConditionDialog!!,
            onDismiss = { showConditionDialog = null },
            onConfirm = { condition ->
                viewModel.returnBook(showConditionDialog!!, condition)
                showConditionDialog = null
            }
        )
    }

    // State Dialogs
    when (val state = borrowState) {
        is BorrowState.Error -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissState,
                title = { Text("Error", color = TextPrimary) },
                text = { Text(state.message, color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissState) {
                        Text("OK", color = PurpleAccent)
                    }
                },
                containerColor = DarkSurface
            )
        }
        is BorrowState.Success -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissState,
                title = { Text("Berhasil", color = StatusGreen) },
                text = { Text(state.message, color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissState) {
                        Text("OK", color = PurpleAccent)
                    }
                },
                containerColor = DarkSurface
            )
        }
        BorrowState.Loading -> { /* Loading indicator */ }
        BorrowState.Idle -> {}
    }
}

@Composable
private fun ReturnBookCard(
    item: TransactionWithBook,
    isAdmin: Boolean,
    onReturnClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(item.transaction.dueDate))
    val isOverdue = item.fineEstimate > 0
    
    // Calculate days remaining/overdue
    val currentTime = System.currentTimeMillis()
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(item.transaction.dueDate - currentTime)
    val progress = if (daysRemaining >= 0) {
        // Assuming 14 days loan period
        (14 - daysRemaining).toFloat() / 14f
    } else {
        1f
    }

    LiquidGlassBookCard(
        title = item.book.title,
        author = item.borrowerName?.let { "Peminjam: $it" } ?: "",
        coverContent = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isOverdue) {
                                listOf(StatusRed.copy(alpha = 0.8f), StatusOrange.copy(alpha = 0.8f))
                            } else {
                                listOf(PurpleAccent, BlueAccent)
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        additionalInfo = {
            Column {
                // Due date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = dueDateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DeadlineProgressBar(
                        progress = progress,
                        isOverdue = isOverdue,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = if (daysRemaining >= 0) {
                            "${daysRemaining}d tersisa"
                        } else {
                            "${-daysRemaining}d terlambat"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverdue) StatusRed else StatusGreen
                    )
                }
                
                // Fine estimate
                if (item.fineEstimate > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estimasi Denda: Rp ${item.fineEstimate.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = StatusRed
                    )
                }
            }
        },
        statusBadge = {
            StatusBadge(
                text = if (isOverdue) "Terlambat" else "On Time",
                color = if (isOverdue) StatusRed else StatusGreen
            )
        },
        trailingContent = {
            LiquidGlassButton(
                text = if (isAdmin) "Denda" else "Kembalikan",
                onClick = onReturnClick,
                isDestructive = isAdmin
            )
        }
    )
}

@Composable
fun ReturnConditionDialog(
    item: TransactionWithBook,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val options = listOf(
        "Rusak (Denda Rp 100.000)" to "Rusak",
        "Hilang (Denda Rp 150.000)" to "Hilang",
        "Robek (Denda Rp 50.000)" to "Robek"
    )
    var selectedOption by remember { mutableStateOf(options[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Pilih Kondisi Denda",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                Text(
                    "Pilih alasan denda untuk buku ini:",
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                options.forEach { (displayName, key) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (key == selectedOption.second),
                            onClick = { selectedOption = displayName to key },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PurpleAccent,
                                unselectedColor = TextTertiary
                            )
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedOption.second) }) {
                Text("Konfirmasi Denda", color = StatusRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp)
    )
}
