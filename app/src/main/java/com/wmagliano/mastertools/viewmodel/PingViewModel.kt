package com.wmagliano.mastertools.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmagliano.mastertools.domain.executor.NhCommandExecutor
import kotlinx.coroutines.launch

class PingViewModel : ViewModel() {

    private val executor = NhCommandExecutor()

    var ipOrHost by mutableStateOf("")
    var output by mutableStateOf("")
    var isRunning by mutableStateOf(false)

    fun updateIpOrHost(value: String) {
        ipOrHost = value
    }

    fun runPing() {
        val target = ipOrHost.trim()

        if (target.isBlank()) {
            output = "Ingresa una IP o host."
            return
        }

        output = "Ejecutando:\nnh -r /root/bin/recon-wrapper.sh ping $target\n"

        viewModelScope.launch {
            isRunning = true
            val buffer = StringBuilder(output)

            try {
                executor.runWrapperCommand(
                    tool = "ping",
                    target = target
                ) { line ->
                    buffer.appendLine(line)
                    output = buffer.toString()
                }
            } catch (e: Exception) {
                buffer.appendLine("Error: ${e.message}")
                output = buffer.toString()
            } finally {
                isRunning = false
            }
        }
    }

    fun cancel() {
        executor.cancel()
        isRunning = false
        output += "\nProceso cancelado."
    }

    override fun onCleared() {
        super.onCleared()
        executor.cancel()
    }
}