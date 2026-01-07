package com.galang.bbooks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.ui.theme.DeepBlue
import com.galang.bbooks.ui.theme.PastelBlue
import com.galang.bbooks.ui.theme.SoftBlue
import com.galang.bbooks.ui.theme.SoftCoral
import com.galang.bbooks.ui.viewmodel.LoginViewModel
import com.galang.bbooks.ui.viewmodel.LoginViewModelFactory

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(app.userRepository)
    )

    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent, // Allow background to show through
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0) // Handle insets manually if needed, or let Scaffold handle content padding
    ) { padding ->
        // Background container fills the ENTIRE screen (ignoring padding initially)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PastelBlue), // Background Color
            contentAlignment = Alignment.Center
        ) {
            // Background Deco Circles
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = (-80).dp, y = (-200).dp)
                    .clip(CircleShape)
                    .background(SoftCoral.copy(alpha = 0.2f))
            )
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = 100.dp, y = (-250).dp)
                    .clip(CircleShape)
                    .background(SoftBlue.copy(alpha = 0.2f))
            )
             Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = 120.dp, y = 200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            // Content Container - Apply padding here to avoid overlapping system bars
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding) // Critical: Apply system bar padding here
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo / Icon
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Logo",
                    tint = DeepBlue,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = DeepBlue
                )
                Text(
                    text = "Sign in to continue reading",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DeepBlue.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                // Inputs
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.username = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SoftBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftBlue,
                        unfocusedBorderColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.8f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SoftBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = SoftBlue
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                     colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftBlue,
                        unfocusedBorderColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.8f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = viewModel.loginError != null) {
                    Text(
                        text = viewModel.loginError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !viewModel.isLoading,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlue,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onRegisterClick) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        color = DeepBlue
                    )
                }
            }
        }
    }
}
