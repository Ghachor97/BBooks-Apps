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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.ui.components.LiquidGlassCard
import com.galang.bbooks.ui.theme.*
import com.galang.bbooks.ui.viewmodel.BookDetailViewModel
import com.galang.bbooks.ui.viewmodel.BookDetailViewModelFactory
import com.galang.bbooks.ui.viewmodel.BorrowState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale

@Composable
fun BookDetailScreen(bookId: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: BookDetailViewModel = viewModel(
        factory = BookDetailViewModelFactory(
            app.bookRepository,
            app.transactionRepository,
            app.userRepository,
            bookId
        )
    )

    val book by viewModel.book.collectAsState()
    val borrowState by viewModel.borrowState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 100.dp, y = (-100).dp)
                    .clip(CircleShape)
                    .background(PurpleAccent.copy(alpha = 0.1f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GlassWhite)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                }

                book?.let { b ->
                    // Book Cover Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        PurpleAccent.copy(alpha = 0.3f),
                                        BlueAccent.copy(alpha = 0.2f),
                                        DarkSurface
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (b.coverUrl.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(b.coverUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Cover ${b.title}",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Fallback: Glow effect + icon
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(PurpleAccent.copy(alpha = 0.2f))
                            )
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Book Title & Author
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = b.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "karya ${b.author}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(4) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = StatusOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4.5",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "(1,245 ulasan)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Info Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoChip(
                            icon = Icons.Default.Category,
                            label = b.category
                        )
                        InfoChip(
                            icon = Icons.Default.Inventory,
                            label = "Stok: ${b.stock}"
                        )
                        InfoChip(
                            icon = Icons.Default.CalendarToday,
                            label = "2023"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Synopsis Card
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        cornerRadius = 20.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "SINOPSIS",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PurpleAccent
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Di antara kehidupan dan kematian terdapat sebuah perpustakaan, dan di dalamnya ada rak-rak yang tak berujung. Setiap buku memberikan kesempatan untuk mencoba kehidupan lain yang bisa saja Anda jalani.\n\nUntuk melihat bagaimana keadaan akan berbeda jika Anda membuat pilihan lain...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Borrow Button
                    val isAvailable = b.stock > 0 && b.isAvailable
                    val isLoading = borrowState is BorrowState.Loading

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                brush = if (isAvailable && !isLoading) {
                                    Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent))
                                } else {
                                    Brush.horizontalGradient(listOf(TextDisabled, TextDisabled))
                                }
                            )
                            .then(
                                if (isAvailable && !isLoading) {
                                    Modifier.background(Color.Transparent)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            TextButton(
                                onClick = { if (isAvailable) viewModel.borrowBook() },
                                modifier = Modifier.fillMaxSize(),
                                enabled = isAvailable
                            ) {
                                Text(
                                    text = if (isAvailable) "PINJAM BUKU" else "STOK HABIS",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    if (!isAvailable && b.stock <= 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Estimasi tersedia: 15 Nov",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Success Dialog
    if (borrowState is BorrowState.Success) {
        AlertDialog(
            onDismissRequest = viewModel::dismissState,
            title = { 
                Text(
                    "Berhasil",
                    color = StatusGreen,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    (borrowState as BorrowState.Success).message,
                    color = TextSecondary
                ) 
            },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.dismissState() 
                    onBack()
                }) {
                    Text("OK", color = PurpleAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Error Dialog
    if (borrowState is BorrowState.Error) {
        AlertDialog(
            onDismissRequest = viewModel::dismissState,
            title = { 
                Text(
                    "Gagal",
                    color = StatusRed,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    (borrowState as BorrowState.Error).message,
                    color = TextSecondary
                ) 
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissState) {
                    Text("OK", color = PurpleAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    label: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GlassWhite)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurpleAccent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}
