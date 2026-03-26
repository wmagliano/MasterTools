package com.wmagliano.mastertools.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wmagliano.mastertools.viewmodel.PingViewModel

@Composable
fun PingTab(viewModel: PingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "PING",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = viewModel.ipOrHost,
            onValueChange = { viewModel.updateIpOrHost(it) },
            label = { Text("IP o Host") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.runPing() },
                enabled = !viewModel.isRunning
            ) {
                Text("Ejecutar")
            }

            Button(
                onClick = { viewModel.cancel() },
                enabled = viewModel.isRunning
            ) {
                Text("Cancelar")
            }
        }

        Text(
            text = if (viewModel.output.isBlank()) "Sin salida todavía." else viewModel.output,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}