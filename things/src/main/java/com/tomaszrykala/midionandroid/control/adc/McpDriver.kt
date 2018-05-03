package com.tomaszrykala.midionandroid.control.adc

import android.os.Handler
import android.util.Log
import com.tomaszrykala.midionandroid.control.CcListener
import java.io.IOException

class McpDriver(csGpio: String, clockGpio: String, mosiGpio: String, misoGpio: String) {

    companion object {
        const val TAG = "MCP3008"
        const val CHANNELS = 8
        const val DELAY_MS = 100L // 0.1 second
    }

    val mcp3008: Mcp3008 = Mcp3008(csGpio, clockGpio, mosiGpio, misoGpio)
    val listeners: MutableMap<Int, CcListener?> = mutableMapOf()
    private lateinit var handler: Handler

    fun start() {
        try {
            mcp3008.register()
        } catch (e: IOException) {
            Log.d(TAG, "MCP initialization exception occurred: " + e.message)
        }

        handler.post(readAdcRunnable)
    }

    fun stop() {
        listeners.clear()
        mcp3008.unregister()
        handler.removeCallbacks(readAdcRunnable)
    }

    fun addListener(analogChannel: Int, listener: CcListener?) {
        listeners[analogChannel] = listener
    }

    private val readAdcRunnable = object : Runnable {

        private val lastReads = IntArray(CHANNELS)

        override fun run() {
            try {
                for (pair in listeners.toList()) {
                    val readAdc = Math.round((mcp3008.readAdc(pair.first) / CHANNELS).toFloat())
                    if (lastReads[pair.first] != readAdc) {
                        lastReads[pair.first] = readAdc
                        Log.d(TAG, "ADC ${pair.first}: $readAdc")
                        listeners[pair.first]?.onChange(readAdc)
                    }
                }
            } catch (e: IOException) {
                Log.d(TAG, "Something went wrong while reading from the ADC: " + e.message)
            }

            handler.postDelayed(this, DELAY_MS)
        }
    }
}
