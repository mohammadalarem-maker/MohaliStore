package com.mohali.store.ui.users

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.*
import com.mohali.store.ui.theme.MohaliColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    onBack: () -> Unit,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }

    val isAdmin = currentUser?.role == UserRole.ADMIN

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("المستخدمون", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MohaliColors.Accent,
                    contentColor = Color.White
                ) { Icon(Icons.Filled.PersonAdd, null) }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Current user card
            item {
                currentUser?.let { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MohaliColors.Accent.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, MohaliColors.Accent.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp).background(MohaliColors.Accent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) { Text(user.username.firstOrNull()?.uppercase() ?: "U", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Box(modifier = Modifier.background(MohaliColors.Accent.copy(0.3f), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(user.role.nameAr, color = MohaliColors.Accent, fontSize = 10.sp)
                                    }
                                }
                                Text("الحساب الحالي", color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
                            }
                            IconButton(onClick = { showChangePasswordDialog = true }) {
                                Icon(Icons.Outlined.Lock, null, tint = MohaliColors.AccentTeal)
                            }
                        }
                    }
                }
            }

            item {
                Text("جميع المستخدمين (${users.size})", color = MohaliColors.OnSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            items(users, key = { it.uid }) { user ->
                UserCard(
                    user = user,
                    isAdmin = isAdmin,
                    onEdit = { editingUser = user },
                    onDeactivate = { viewModel.deactivateUser(user.uid) }
                )
            }
        }
    }

    // Add user dialog
    if (showAddDialog) {
        AddUserDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { username, email, password, role ->
                viewModel.addUser(username, email, password, role)
                showAddDialog = false
            }
        )
    }

    // Change password dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onChange = { currentPass, newPass ->
                viewModel.changePassword(newPass) { success, error ->
                    if (success) showChangePasswordDialog = false
                }
            }
        )
    }
}

@Composable
fun UserCard(user: User, isAdmin: Boolean, onEdit: () -> Unit, onDeactivate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isActive) MohaliColors.Card else MohaliColors.Surface
        ),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).background(
                    MohaliColors.getRoleColor(user.role).copy(alpha = 0.15f), CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.username.firstOrNull()?.uppercase() ?: "U",
                    color = MohaliColors.getRoleColor(user.role),
                    fontWeight = FontWeight.Bold, fontSize = 17.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(user.username, color = if (user.isActive) Color.White else MohaliColors.Muted, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    if (!user.isActive)
                        Box(Modifier.background(MohaliColors.Error.copy(0.15f), RoundedCornerShape(5.dp)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                            Text("معطّل", color = MohaliColors.Error, fontSize = 9.sp)
                        }
                }
                Text(user.email, color = MohaliColors.Muted, fontSize = 11.sp)
                Box(Modifier.background(MohaliColors.getRoleColor(user.role).copy(0.12f), RoundedCornerShape(5.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(user.role.nameAr, color = MohaliColors.getRoleColor(user.role), fontSize = 10.sp)
                }
            }
            if (isAdmin && user.role != UserRole.ADMIN) {
                IconButton(onClick = onDeactivate, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (user.isActive) Icons.Filled.Block else Icons.Filled.CheckCircle,
                        null,
                        tint = if (user.isActive) MohaliColors.Error else MohaliColors.Success,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

fun MohaliColors.getRoleColor(role: UserRole): Color = when (role) {
    UserRole.ADMIN   -> Accent
    UserRole.MANAGER -> AccentTeal
    UserRole.CASHIER -> Success
    UserRole.VIEWER  -> Muted
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddUserDialog(onDismiss: () -> Unit, onAdd: (String, String, String, UserRole) -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CASHIER) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MohaliColors.Card,
        title = { Text("إضافة مستخدم جديد", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("اسم المستخدم") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("البريد الإلكتروني") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("كلمة المرور") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), visualTransformation = PasswordVisualTransformation(), colors = dialogFieldColors())
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(value = selectedRole.nameAr, onValueChange = {}, readOnly = true, label = { Text("الصلاحية") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MohaliColors.Card)) {
                        UserRole.values().filter { it != UserRole.ADMIN }.forEach { role ->
                            DropdownMenuItem(text = { Text(role.nameAr, color = Color.White) }, onClick = { selectedRole = role; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(username, email, password, selectedRole) }, colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Accent)) {
                Text("إضافة", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = MohaliColors.OnSurfaceVariant) }
        }
    )
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit, onChange: (String, String) -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MohaliColors.Card,
        title = { Text("تغيير كلمة المرور", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (error.isNotEmpty()) Text(error, color = MohaliColors.Error, fontSize = 12.sp)
                OutlinedTextField(value = currentPassword, onValueChange = { currentPassword = it }, label = { Text("كلمة المرور الحالية") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), visualTransformation = PasswordVisualTransformation(), colors = dialogFieldColors())
                OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("كلمة المرور الجديدة") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), visualTransformation = PasswordVisualTransformation(), colors = dialogFieldColors())
                OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("تأكيد كلمة المرور") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), visualTransformation = PasswordVisualTransformation(), colors = dialogFieldColors())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword != confirmPassword) { error = "كلمتا المرور غير متطابقتان"; return@Button }
                    if (newPassword.length < 6) { error = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"; return@Button }
                    onChange(currentPassword, newPassword)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Accent)
            ) { Text("تغيير", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = MohaliColors.OnSurfaceVariant) }
        }
    )
}

@Composable
fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MohaliColors.Accent, unfocusedBorderColor = MohaliColors.CardBorder,
    focusedLabelColor = MohaliColors.Accent, unfocusedLabelColor = MohaliColors.OnSurfaceVariant,
    cursorColor = MohaliColors.Accent, focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    focusedContainerColor = MohaliColors.SurfaceVariant, unfocusedContainerColor = MohaliColors.Surface
)
