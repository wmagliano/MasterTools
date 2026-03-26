package com.wmagliano.mastertools.domain.executor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class NhCommandExecutor {

    private var process: Process? = null

    suspend fun runWrapperCommand(
        tool: String,
        target: String,
        onOutput: (String) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        val builder = ProcessBuilder(
            "su",
            "-c",
            "nh -r /root/bin/recon-wrapper.sh $tool $target"
        ).redirectErrorStream(true)

        process = builder.start()

        BufferedReader(InputStreamReader(process!!.inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                onOutput(line.orEmpty())
            }
        }

        process?.waitFor() ?: -1
    }

    fun cancel() {
        process?.destroy()
        process = null
    }
}