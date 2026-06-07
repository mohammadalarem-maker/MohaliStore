package com.mohali.store.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.mohali.store.ui.theme.MohaliColors
import kotlinx.coroutines.delay
import android.widget.Toast
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (isAdmin: Boolean) -> Unit,
    viewModel: LoginViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ), label = "offset"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    var formVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        formVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MohaliColors.LoginBg1,
                        MohaliColors.LoginBg2,
                        Color(0xFF1A1A3E)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAnimatedBackground(animOffset)
        }

        ParticleBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            AnimatedVisibility(
                visible = formVisible,
                enter = slideInVertically(
                    initialOffsetY = { -100 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .scale(pulse)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MohaliColors.Accent,
                                        MohaliColors.Secondary
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = MohaliColors.Accent.copy(alpha = 0.4f),
                                spotColor = MohaliColors.Accent.copy(alpha = 0.4f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "محلي ستور",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "نظام إدارة المحل المتكامل",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MohaliColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, MohaliColors.Accent)
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(MohaliColors.Accent, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(MohaliColors.Accent, Color.Transparent)
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = formVisible,
                enter = slideInVertically(
                    initialOffsetY = { 200 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(
                                    MohaliColors.Accent.copy(alpha = 0.5f),
                                    MohaliColors.AccentTeal.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            RoundedCornerShape(28.dp)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MohaliColors.LoginCard
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "تسجيل الدخول",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "أدخل بياناتك للدخول إلى النظام",
                            style = MaterialTheme.typography.bodySmall,
                            color = MohaliColors.OnSurfaceVariant
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; errorMessage = "" },
                            label = { Text("اسم المستخدم") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = if (username.isNotEmpty()) MohaliColors.Accent
                                    else MohaliColors.OnSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MohaliColors.Accent,
                                unfocusedBorderColor = MohaliColors.CardBorder,
                                focusedLabelColor = MohaliColors.Accent,
                                unfocusedLabelColor = MohaliColors.OnSurfaceVariant,
                                cursorColor = MohaliColors.Accent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = MohaliColors.SurfaceVariant,
                                unfocusedContainerColor = MohaliColors.Surface
                            ),
                            singleLine = true,
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = "" },
                            label = { Text("كلمة المرور") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = if (password.isNotEmpty()) MohaliColors.Accent
                                    else MohaliColors.OnSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                                        else Icons.Outlined.Visibility,
                                        contentDescription = null,
                                        tint = MohaliColors.OnSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MohaliColors.Accent,
                                unfocusedBorderColor = MohaliColors.CardBorder,
                                focusedLabelColor = MohaliColors.Accent,
                                unfocusedLabelColor = MohaliColors.OnSurfaceVariant,
                                cursorColor = MohaliColors.Accent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = MohaliColors.SurfaceVariant,
                                unfocusedContainerColor = MohaliColors.Surface
                            ),
                            singleLine = true,
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                        )

                        AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MohaliColors.Error.copy(alpha = 0.1f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Icon(
                                    Icons.Filled.ErrorOutline,
                                    contentDescription = null,
                                    tint = MohaliColors.Error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = errorMessage,
                                    color = MohaliColors.Error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Login Button (المعدل لحصار الكراش بالكامل)
                        Button(
                            onClick = {
                                try {
                                    if (username.isEmpty() || password.isEmpty()) {
                                        errorMessage = "الرجاء إدخال اسم المستخدم وكلمة المرور"
                                        return@Button
                                    }
                                    isLoading = true
                                    viewModel.login(username, password) { success, isAdmin, error ->
                                        try {
                                            isLoading = false
                                            if (success) {
                                                onLoginSuccess(isAdmin)
                                            } else {
                                                errorMessage = error ?: "خطأ في تسجيل الدخول"
                                                Toast.makeText(context, "فشل: $errorMessage", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (callbackBug: Throwable) {
                                            isLoading = false
                                            errorMessage = "خطأ الانتقال: ${callbackBug.message}"
                                            Toast.makeText(context, "الكراش عند الانتقال للشاشة التالية!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } catch (uiBug: Throwable) {
                                    isLoading = false
                                    errorMessage = "خطأ واجهة: ${uiBug.message}"
                                    Toast.makeText(context, "كراش الزر: ${uiBug.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            enabled = !isLoading
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(MohaliColors.Accent, Color(0xFFAF2D50))
                                        ),
                                        RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Login,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Text(
                                            text = "تسجيل الدخول",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = formVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 600))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "تطوير: كلود",
                        style = MaterialTheme.typography.labelSmall,
                        color = MohaliColors.Muted
                    )
                    Text(
                        text = "بواسطة محمد الصارم",
                        style = MaterialTheme.typography.labelSmall,
                        color = MohaliColors.AccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

private fun DrawScope.drawAnimatedBackground(angle: Float) {
    val centerX = this.size.width / 2
    val centerY = this.size.height / 2

    listOf(
        Triple(centerX - 100f, centerY - 200f, 300f),
        Triple(centerX + 150f, centerY + 100f, 250f),
        Triple(centerX - 50f, centerY + 300f, 200f)
    ).forEachIndexed { i, (cx, cy, radius) ->
        val alpha = (0.08f + 0.03f * sin(Math.toRadians((angle + i * 120).toDouble()))).toFloat()
        drawCircle(
            color = if (i % 2 == 0) Color(0xFFE94560) else Color(0xFF00B4D8),
            radius = radius,
            center = Offset(cx, cy),
            alpha = alpha.coerceIn(0f, 1f)
        )
    }
}

@Composable
private fun ParticleBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val particles = remember {
        (0..20).map {
            Triple(
                (Math.random() * 400 - 200).toFloat(),
                (Math.random() * 800).toFloat(),
                (Math.random() * 4 + 2).toFloat()
            )
        }
    }

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing)
        ), label = "particleY"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEachIndexed { i, (x, y, size) ->
            val currentY = (y + offsetY * size * 100) % this.size.height
            drawCircle(
                color = Color.White.copy(alpha = 0.05f + (i % 5) * 0.01f),
                radius = size,
                center = Offset(this.size.width / 2 + x, currentY)
            )
        }
    }
}
