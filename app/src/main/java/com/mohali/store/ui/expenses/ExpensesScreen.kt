package com.mohali.store.ui.expenses

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohali.store.data.models.Expense
import com.mohali.store.data.models.ExpenseCategory
import com.mohali.store.ui.theme.MohaliColors
import com.mohali.store.ui.users.dialogFieldColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(onBack: () -> Unit, viewModel: ExpensesViewModel = hiltViewModel()) {
    val expenses by viewModel.expenses.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MohaliColors.Primary,
        topBar = {
            TopAppBar(
                title = { Text("المصروفات", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MohaliColors.PrimaryVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MohaliColors.Error) {
                Icon(Icons.Filled.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MohaliColors.Error.copy(0.1f)),
                    border = BorderStroke(1.dp, MohaliColors.Error.copy(0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("إجمالي المصروفات (الشهر)", color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
                        Text("${"%.2f".format(totalExpenses)} ر", color = MohaliColors.Error, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }
                }
            }

            items(expenses, key = { it.id }) { expense ->
                ExpenseCard(expense, dateFormat, onDelete = { viewModel.deleteExpense(it) })
            }

            if (expenses.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.AccountBalance, null, tint = MohaliColors.Muted, modifier = Modifier.size(60.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("لا توجد مصروفات", color = MohaliColors.Muted)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, amount, category, desc ->
                viewModel.addExpense(title, amount, category, desc)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ExpenseCard(expense: Expense, dateFormat: SimpleDateFormat, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MohaliColors.Card),
        border = BorderStroke(1.dp, MohaliColors.CardBorder)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(expense.category.nameAr, color = MohaliColors.OnSurfaceVariant, fontSize = 12.sp)
                Text(dateFormat.format(Date(expense.createdAt)), color = MohaliColors.Muted, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${"%.2f".format(expense.amount)} ر", color = MohaliColors.Error, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IconButton(onClick = { onDelete(expense.id) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Delete, null, tint = MohaliColors.Error.copy(0.6f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddExpenseDialog(onDismiss: () -> Unit, onAdd: (String, Double, ExpenseCategory, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.OTHER) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MohaliColors.Card,
        title = { Text("إضافة مصروف", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان المصروف *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("المبلغ *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors(), keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(value = selectedCategory.nameAr, onValueChange = {}, readOnly = true, label = { Text("الفئة") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MohaliColors.Card)) {
                        ExpenseCategory.values().forEach { cat ->
                            DropdownMenuItem(text = { Text(cat.nameAr, color = Color.White) }, onClick = { selectedCategory = cat; expanded = false })
                        }
                    }
                }
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("الوصف") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = dialogFieldColors(), maxLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: return@Button
                    if (title.isNotEmpty()) onAdd(title, amt, selectedCategory, description)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MohaliColors.Error)
            ) { Text("إضافة", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = MohaliColors.OnSurfaceVariant) } }
    )
}
