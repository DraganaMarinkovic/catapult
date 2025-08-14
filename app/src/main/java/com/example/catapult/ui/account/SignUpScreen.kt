package com.example.catapult.ui.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.catapult.ui.theme.MyApplicationTheme
import com.example.catapult.viewmodel.account.SignUpEvent
import com.example.catapult.viewmodel.account.SignUpViewModel

@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    vm: SignUpViewModel = hiltViewModel()
) {
    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Create account",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = vm.firstName,
                            onValueChange = { vm.firstName = it },
                            label = { Text("Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.surface,
                                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            )
                        )

                        OutlinedTextField(
                            value = vm.lastName,
                            onValueChange = { vm.lastName = it },
                            label = { Text("Last name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.surface,
                                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            )
                        )

                        OutlinedTextField(
                            value = vm.nickname,
                            onValueChange = { vm.nickname = it },
                            label = { Text("Username") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.surface,
                                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            )

                        )

                        OutlinedTextField(
                            value = vm.email,
                            onValueChange = { vm.email = it },
                            label = { Text("Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.surface,
                                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            )
                        )

                        vm.errorMsg?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { vm.sendEvent(SignUpEvent.Submit(onSignedUp)) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save")
                }
            }
        }
    }
}
