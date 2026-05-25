package com.example.realestateapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realestateapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

enum class AuthScreen { LOGIN, REGISTER, FORGOT }

@Composable
fun LoginScreen(onLoginSuccess: (role: String) -> Unit) {
    var screen by remember { mutableStateOf(AuthScreen.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var success by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1B2A), Color(0xFF1A3C5E))
                )
            )
    ) {
        // Background decorative circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x33DAA520), Color.Transparent)
                    ),
                    RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x224A90D9), Color.Transparent)
                    ),
                    RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(60.dp))

            // App logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(28.dp))
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Ethiopia Real Estate",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                when (screen) {
                    AuthScreen.LOGIN -> "Welcome back, sign in to continue"
                    AuthScreen.REGISTER -> "Create your account to get started"
                    AuthScreen.FORGOT -> "Enter your email to reset password"
                },
                fontSize = 13.sp,
                color = Color(0xFF4A90D9),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Screen title inside card
                    Text(
                        when (screen) {
                            AuthScreen.LOGIN -> "Sign In"
                            AuthScreen.REGISTER -> "Sign Up"
                            AuthScreen.FORGOT -> "Forgot Password"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D1B2A)
                    )
                    Spacer(Modifier.height(4.dp))
                    // Gold underline accent
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFDAA520))
                    )

                    Spacer(Modifier.height(20.dp))

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim() },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFFDAA520)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFDAA520),
                            unfocusedBorderColor = Color(0xFFDDDDDD),
                            focusedLabelColor = Color(0xFFDAA520),
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color(0xFF0D1B2A),
                            unfocusedTextColor = Color(0xFF0D1B2A),
                            cursorColor = Color(0xFFDAA520)
                        ),
                        singleLine = true
                    )

                    // Password field
                    if (screen != AuthScreen.FORGOT) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFDAA520)
                                )
                            },
                            trailingIcon = {
                                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(
                                        if (passwordVisible) "Hide" else "Show",
                                        color = Color(0xFF1A3C5E),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDAA520),
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                focusedLabelColor = Color(0xFFDAA520),
                                unfocusedLabelColor = Color.Gray,
                                focusedTextColor = Color(0xFF0D1B2A),
                                unfocusedTextColor = Color(0xFF0D1B2A),
                                cursorColor = Color(0xFFDAA520)
                            ),
                            singleLine = true
                        )
                    }

                    // Confirm password field
                    if (screen == AuthScreen.REGISTER) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFDAA520)
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDAA520),
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                focusedLabelColor = Color(0xFFDAA520),
                                unfocusedLabelColor = Color.Gray,
                                focusedTextColor = Color(0xFF0D1B2A),
                                unfocusedTextColor = Color(0xFF0D1B2A),
                                cursorColor = Color(0xFFDAA520)
                            ),
                            singleLine = true
                        )
                    }

                    // Error / success messages
                    if (error.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Text(
                                error,
                                color = Color(0xFFD32F2F),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                    if (success.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Text(
                                success,
                                color = Color(0xFF2E7D32),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Main action button
                    if (loading) {
                        CircularProgressIndicator(color = Color(0xFFDAA520))
                    } else {
                        Button(
                            onClick = {
                                error = ""; success = ""
                                when (screen) {
                                    AuthScreen.LOGIN -> {
                                        if (email.isEmpty() || password.isEmpty()) {
                                            error = "Please fill all fields"
                                            return@Button
                                        }
                                        loading = true
                                        Firebase.auth.signInWithEmailAndPassword(email, password)
                                            .addOnSuccessListener { authResult ->
                                                val uid = authResult.user!!.uid
                                                val userEmail = authResult.user!!.email ?: ""
                                                Firebase.firestore.collection("users")
                                                    .document(uid).get()
                                                    .addOnSuccessListener { doc ->
                                                        loading = false
                                                        val firestoreRole = doc.getString("role") ?: "user"
                                                        // Email-based fallback in case Firestore role is wrong
                                                        val role = if (userEmail == "admin@realestate.com") "admin"
                                                        else firestoreRole.trim().lowercase()
                                                        onLoginSuccess(role)
                                                    }
                                                    .addOnFailureListener {
                                                        loading = false
                                                        // Fallback to email check if Firestore fails
                                                        val role = if (userEmail == "admin@realestate.com") "admin" else "user"
                                                        onLoginSuccess(role)
                                                    }
                                            }
                                            .addOnFailureListener {
                                                loading = false
                                                error = it.message ?: "Login failed"
                                            }
                                    }

                                    AuthScreen.REGISTER -> {
                                        when {
                                            email.isEmpty() || password.isEmpty() ->
                                                error = "Please fill all fields"
                                            password != confirmPassword ->
                                                error = "Passwords do not match"
                                            password.length < 6 ->
                                                error = "Password must be at least 6 characters"
                                            else -> {
                                                loading = true
                                                Firebase.auth.createUserWithEmailAndPassword(email, password)
                                                    .addOnSuccessListener { result ->
                                                        val uid = result.user!!.uid
                                                        val role = if (email == "admin@realestate.com") "admin" else "user"
                                                        Firebase.firestore.collection("users")
                                                            .document(uid)
                                                            .set(mapOf("role" to role, "email" to email))
                                                            .addOnSuccessListener {
                                                                loading = false
                                                                onLoginSuccess(role)
                                                            }
                                                            .addOnFailureListener {
                                                                loading = false
                                                                onLoginSuccess(role)
                                                            }
                                                    }
                                                    .addOnFailureListener {
                                                        loading = false
                                                        error = it.message ?: "Registration failed"
                                                    }
                                            }
                                        }
                                    }
                                    AuthScreen.FORGOT -> {
                                        if (email.isEmpty()) {
                                            error = "Enter your email"
                                            return@Button
                                        }
                                        loading = true
                                        Firebase.auth.sendPasswordResetEmail(email)
                                            .addOnSuccessListener {
                                                loading = false
                                                success = "✅ Reset link sent to $email"
                                            }
                                            .addOnFailureListener {
                                                loading = false
                                                error = it.message ?: "Failed"
                                            }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0D1B2A),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                when (screen) {
                                    AuthScreen.LOGIN -> "Sign In"
                                    AuthScreen.REGISTER -> "Create Account"
                                    AuthScreen.FORGOT -> "Send Reset Link"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Bottom navigation links
                        when (screen) {
                            AuthScreen.LOGIN -> {
                                // Forgot password
                                TextButton(
                                    onClick = { screen = AuthScreen.FORGOT; error = ""; success = "" }
                                ) {
                                    Text(
                                        "Forgot Password?",
                                        color = Color(0xFFB8860B),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                // Divider with text
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
                                    Text(
                                        "  OR  ",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
                                }
                                Spacer(Modifier.height(4.dp))
                                // Register button
                                OutlinedButton(
                                    onClick = { screen = AuthScreen.REGISTER; error = ""; success = "" },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF1A3C5E)
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        width = 1.5.dp
                                    )
                                ) {
                                    Text(
                                        "Create New Account",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            AuthScreen.REGISTER -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
                                    Text("  OR  ", color = Color.Gray, fontSize = 12.sp)
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
                                }
                                Spacer(Modifier.height(4.dp))
                                OutlinedButton(
                                    onClick = { screen = AuthScreen.LOGIN; error = ""; success = "" },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF1A3C5E)
                                    )
                                ) {
                                    Text(
                                        "Already have an account? Sign In",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            AuthScreen.FORGOT -> {
                                TextButton(
                                    onClick = { screen = AuthScreen.LOGIN; error = ""; success = "" }
                                ) {
                                    Text(
                                        "← Back to Sign In",
                                        color = Color(0xFF1A3C5E),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
