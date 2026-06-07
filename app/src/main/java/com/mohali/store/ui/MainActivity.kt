package com.mohali.store.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mohali.store.ui.navigation.AppNavigation
import com.mohali.store.ui.theme.MohaliColors
import com.mohali.store.ui.theme.MohaliStoreTheme
import com.mohali.store.utils.CrashLogger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            CrashLogger.log(this, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MohaliStoreTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = MohaliColors.Primary,
                        darkIcons = false
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    var crashText by remember { 
                        mutableStateOf(context.getSharedPreferences("mohali_debug", Context.MODE_PRIVATE).getString("last_crash", null)) 
                    }

                    // إذا وجدنا كراش سابق مخزن، نعرضه للمستخدم فوراً في واجهة واضحة
                    if (crashText != null) {
                        AlertDialog(
                            onDismissRequest = { },
                            title = { Text("🚨 تم رصد سبب الكراش الحقيقي!") },
                            text = {
                                LazyColumn {
                                    item {
                                        Text(
                                            text = crashText!!,
                                            color = Color.Red,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    context.getSharedPreferences("mohali_debug", Context.MODE_PRIVATE).edit().remove("last_crash").apply()
                                    crashText = null
                                }) {
                                    Text("مسح ومتابعة التشغيل")
                                }
                            }
                        )
                    } else {
                        val navController = rememberNavController()
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }
}
