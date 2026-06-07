package com.mohali.store.ui.customers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.Customer
import com.mohali.store.ui.theme.MohaliColors
import com.mohali.store.ui.users.dialogFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    onBack: () -> Unit,
    viewModel: CustomersViewModel = hiltViewModel()
) {
    val customers by viewModel.customers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("العملاء", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MohaliColors.Accent) {
                Icon(Icons.Filled.PersonAdd, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery, onValueChange = viewModel::onSearch,
                placeholder = { Text("بحث بالاسم أو الهاتف...") },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = MohaliColors.OnSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MohaliColors.Accent, unfocusedBorderColor = MohaliColors.CardBorder,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = MohaliColors.Surface, unfocusedContainerColor = MohaliColors.Surface
                ), singleLine = true
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(customers, key = { it.id }) { customer ->
                    CustomerCard(customer)
                }
                if (customers.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.People, null, tint = MohaliColors.Muted, modifier = Modifier.size(60.dp))
                                Spacer(Modifier.height(12.dp))
                                Text("لا يوجد عملاء", color = MohaliColors.Muted)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomerDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, email ->
                viewModel.addCustomer(name, phone, email)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CustomerCard(customer: Customer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(46.dp).background(MohaliColors.AccentTeal.copy(0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(customer.name.firstOrNull()?.uppercase() ?: "ع", color = MohaliColors.AccentTeal, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (customer.phone.isNotEmpty()) Text(customer.phone, color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
                Text("${customer.purchaseCount} مشتريات • ${"%.2f".format(customer.totalPurchases)} ر", color = MohaliColors.Accent, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Icon(Icons.Filled.Star, null, tint = MohaliColors.AccentGold, modifier = Modifier.size(18.dp))
                Text("${customer.loyaltyPoints} نقطة", color = MohaliColors.AccentGold, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun AddCustomerDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MohaliColors.Card,
        title = { Text("إضافة عميل جديد", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم العميل *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم الهاتف") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("البريد الإلكتروني") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotEmpty()) onAdd(name, phone, email) }, colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Accent)) {
                Text("إضافة", color = Color.White)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = MohaliColors.OnSurfaceVariant) } }
    )
}
