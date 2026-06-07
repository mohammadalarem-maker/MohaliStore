package com.mohali.store.ui.notifications

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.AppNotification
import com.mohali.store.data.models.NotificationType
import com.mohali.store.ui.theme.MohaliColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit, viewModel: NotificationsViewModel = hiltViewModel()) {
    val notifications by viewModel.notifications.collectAsState()
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("الإشعارات", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) } },
                actions = {
                    if (notifications.isNotEmpty())
                        TextButton(onClick = { viewModel.clearAll() }) { Text("مسح الكل", color = MohaliColors.Error, fontSize = 12.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.NotificationsNone, null, tint = MohaliColors.Muted, modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("لا توجد إشعارات", color = MohaliColors.Muted, style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    NotificationCard(notification, dateFormat)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: AppNotification, dateFormat: SimpleDateFormat) {
    val (color, icon) = when (notification.type) {
        NotificationType.SALE      -> Pair(MohaliColors.Success, Icons.Filled.ShoppingCart)
        NotificationType.LOW_STOCK -> Pair(MohaliColors.Warning, Icons.Filled.Warning)
        NotificationType.PURCHASE  -> Pair(MohaliColors.Info, Icons.Filled.LocalShipping)
        NotificationType.EXPENSE   -> Pair(MohaliColors.Error, Icons.Filled.AccountBalance)
        NotificationType.SYSTEM    -> Pair(MohaliColors.AccentTeal, Icons.Filled.Info)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) color.copy(alpha = 0.08f) else MohaliColors.Card
        ),
        border = BorderStroke(1.dp, if (!notification.isRead) color.copy(alpha = 0.3f) else MohaliColors.CardBorder)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(22.dp)) }
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text(notification.body, color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(6.dp))
                Text(dateFormat.format(Date(notification.createdAt)), color = MohaliColors.Muted, fontSize = 10.sp)
                // Extra data
                notification.data["total"]?.let {
                    Text("المبلغ: $it ر", color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            if (!notification.isRead) {
                Box(modifier = Modifier.size(8.dp).background(color, androidx.compose.foundation.shape.CircleShape))
            }
        }
    }
}
