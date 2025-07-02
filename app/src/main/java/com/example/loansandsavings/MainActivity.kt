package com.example.loansandsavings

import android.os.Bundle
import android.util.EventLogTags
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Room
import com.example.loansandsavings.db.Loan
import com.example.loansandsavings.db.AppDatabase
import com.example.loansandsavings.db.Saving
import com.example.loansandsavings.ui.theme.LoansAndSavingsTheme
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoansAndSavingsTheme {
                Navigator()
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Box(){
        Image(
            painter = painterResource(id = R.drawable.homebackground),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds

        )
    }
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
            navController.navigate("issue_loans")
        }) {
            Text("Issue Loans")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate("view_loans") }) {
            Text("View Loans")
        }


        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate("update_savings") }) {
            Text("Update Savings")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate("view_savings") }) {
            Text("View Savings")
        }
    }
}

@Composable
fun LoansIssueScreen() {
    var names by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(500.0) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
    val dao = db.loansDao()

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = names,
                onValueChange = { names = it },
                label = { Text("Names") }
            )


            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = amount.toString(),
                onValueChange = { amount = it.toDoubleOrNull() ?: 500.0 },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Button(onClick = {
                if (names.isNotBlank() && amount > 0) {
                    coroutineScope.launch {
                        val loan = Loan(
                            names = names,
                            amount = amount.toFloat(),
                            date = System.currentTimeMillis()
                        )
                        dao.save(loan)
                        Log.d("LOANS_DATA", "Count: ${dao.count()}")
                        dao.fetch().forEach{
                            Log.d("LOANS_DATA", "LoansIssueScreen: ${it.names} ${it.amount}")
                        }
                        names = ""
                        amount = 500.0
                    }
                }
            }) { Text("Add Loan") }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }


}


@Composable
fun DisplayLoansScreen() {
    var loansList by remember { mutableStateOf(emptyList<Loan>()) }

    val context = LocalContext.current
    val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
    val dao = db.loansDao()

    val coroutine = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loansList = dao.fetch()
        Log.d("LOANS_DATA", "DisplayLoansScreen: $loansList")
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            items(loansList) { loan ->
                Column() {
                    Text(loan.names, fontWeight = FontWeight.Bold)
                    Text(
                        "Ksh" + NumberFormat.getNumberInstance().format(loan.amount),
                        fontSize = 15.sp
                    )
                    Text("${convertDate(loan.date)}", fontSize = 15.sp)
                    if (!loan.paid) {
                        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                            coroutine.launch {
                                dao.updateLoan(loan.id, true)
                                loansList = dao.fetch()
                            }
                        }) { Text("Repay") }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}


@Composable
fun EnterSavingsScreen() {
    var amount by remember { mutableStateOf(100.0) }
    var description by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
    val dao = db.savingDao()


    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {


            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = amount.toString(),
                onValueChange = { amount = it.toDoubleOrNull() ?: 100.0 },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") })


            Button(onClick = {
                if (amount > 0 && description.isNotBlank()) {
                    coroutineScope.launch {
                        val save = Saving(
                            amount = amount.toFloat(),
                            date = System.currentTimeMillis(),
                            description = description
                        )
                        dao.insertSaving(save)
                        amount = 0.0
                        description=""
                    }
                }
            }) { Text("Add Savings") }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }


}


@Composable
fun DisplaySavingsScreen() {
    var savingList by remember { mutableStateOf(emptyList<Saving>()) }


    val context = LocalContext.current
    val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
    val dao = db.savingDao()

    val coroutine = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        savingList = dao.getAllSavings()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            items(savingList) { saving ->
                Column() {
                    Text(
                        "Ksh" + NumberFormat.getNumberInstance().format(saving.amount),
                        fontSize = 15.sp
                    )
                    Text("${convertDate(saving.date)}", fontSize = 15.sp)
                    Text(
                        "Description: ${saving.description}",
                        fontSize = 14.sp
                    )
                }
                HorizontalDivider()
            }
        }
    }
}

fun convertDate(date: Long): String {
    val d = Date(date)
    val f = SimpleDateFormat("E dd-MM-yyyy H:m a")
    return f.format(d)
}



