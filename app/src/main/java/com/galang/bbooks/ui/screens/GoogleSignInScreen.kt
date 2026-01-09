package com.galang.bbooks.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.R
import com.galang.bbooks.ui.components.LiquidGlassCard
import com.galang.bbooks.ui.theme.*
import com.galang.bbooks.ui.viewmodel.GoogleSignInViewModel
import com.galang.bbooks.ui.viewmodel.GoogleSignInViewModelFactory

@Composable
fun GoogleSignInScreen(
    onSignInSuccess: () -> Unit,
    webClientId: String
) {
    val context = LocalContext.current
    val activity = context as Activity
    val app = context.applicationContext as BBooksApplication
    val viewModel: GoogleSignInViewModel = viewModel(
        factory = GoogleSignInViewModelFactory(app.userRepository, webClientId)
    )
    
    // Activity result launcher for Google Sign-In
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result, onSignInSuccess)
    }

    Scaffold(
        containerColor = DarkBackground,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            // Decorative Background Circles
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = (-100).dp, y = (-250).dp)
                    .clip(CircleShape)
                    .background(PurpleAccent.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = 150.dp, y = (-200).dp)
                    .clip(CircleShape)
                    .background(BlueAccent.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = 100.dp, y = 300.dp)
                    .clip(CircleShape)
                    .background(PurpleAccent.copy(alpha = 0.06f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo with Gradient Background
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PurpleAccent, BlueAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Selamat Datang!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp
                    ),
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Masuk untuk mulai membaca",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(48.dp))

                // Sign In Card
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nikmati pengalaman membaca terbaik dengan BBooks",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        // Google Sign In Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(
                                    if (!viewModel.isLoading) {
                                        Color.White
                                    } else {
                                        TextDisabled
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = PurpleAccent
                                )
                            } else {
                                TextButton(
                                    onClick = { 
                                        val signInIntent = viewModel.getSignInIntent(activity)
                                        signInLauncher.launch(signInIntent)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        // Google Icon
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_google),
                                            contentDescription = "Google",
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Masuk dengan Google",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = Color.Black.copy(alpha = 0.87f)
                                        )
                                    }
                                }
                            }
                        }

                        // Error Message
                        AnimatedVisibility(visible = viewModel.errorMessage != null) {
                            Text(
                                text = viewModel.errorMessage ?: "",
                                color = StatusRed,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Dengan masuk, Anda menyetujui Syarat & Ketentuan kami",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
