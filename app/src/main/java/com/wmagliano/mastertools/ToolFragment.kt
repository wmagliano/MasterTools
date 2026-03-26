package com.wmagliano.mastertools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.concurrent.thread

class ToolFragment : Fragment() {

    private lateinit var toolName: String
    private lateinit var targetInput: EditText
    private lateinit var runButton: Button
    private lateinit var outputView: TextView

    companion object {
        private const val ARG_TOOL = "tool"

        fun newInstance(tool: String): ToolFragment {
            val fragment = ToolFragment()
            val args = Bundle()
            args.putString(ARG_TOOL, tool)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolName = arguments?.getString(ARG_TOOL) ?: "Tool"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        targetInput = view.findViewById(R.id.targetInput)
        runButton = view.findViewById(R.id.runButton)
        outputView = view.findViewById(R.id.outputView)

        runButton.text = "Ejecutar $toolName"

        runButton.setOnClickListener {
            val target = targetInput.text.toString().trim()

            if (target.isEmpty()) {
                outputView.text = "Debes indicar un objetivo."
                return@setOnClickListener
            }

            runButton.isEnabled = false
            outputView.text = "Ejecutando $toolName...\n\n"

            thread {
                runTool(target)
                activity?.runOnUiThread {
                    runButton.isEnabled = true
                }
            }
        }
    }

    private fun shellQuote(value: String): String {
        return "'" + value.replace("'", "'\\''") + "'"
    }

    private fun runCommandInNetHunter(
        command: String,
        onOutput: (String) -> Unit,
        onFinished: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val fullCommand = "/data/local/nhsystem/nh_run.sh ${shellQuote(command)}"
        val cmd = arrayOf("su", "-c", fullCommand)

        try {
            val process = ProcessBuilder(*cmd)
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    activity?.runOnUiThread {
                        onOutput(line)
                    }
                }
            }

            val exitCode = process.waitFor()

            activity?.runOnUiThread {
                onFinished(exitCode)
            }
        } catch (e: Exception) {
            activity?.runOnUiThread {
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    private fun buildCommand(target: String): String {
        return when (toolName) {
            "Nmap" -> "/root/bin/puertos.sh $target"
            "WhatWeb" -> "/root/bin/queweb.sh $target"
            "TestSSL" -> "/root/bin/pruebassl.sh $target"
            "MasterTools" -> "/root/bin/mastertools.sh ${shellQuote(target)}"
            else -> "echo Herramienta no soportada"
        }
    }

    private fun runTool(target: String) {
        val command = buildCommand(target)

        runCommandInNetHunter(
            command = command,
            onOutput = { line ->
                outputView.append(line + "\n")
            },
            onFinished = { exitCode ->
                outputView.append("\nExit code: $exitCode\n")
            },
            onError = { error ->
                outputView.append("\nError: $error\n")
            }
        )
    }
}