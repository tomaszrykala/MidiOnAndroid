package com.tomaszrykala.midionandroid.control.adc

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException

class Mcp3008(private val csGpio: String,
              private val clockGpio: String,
              private val mosiGpio: String,
              private val misoGpio: String) {

    private lateinit var csPin: Gpio
    private lateinit var clockPin: Gpio
    private lateinit var mosiPin: Gpio
    private lateinit var misoPin: Gpio

    private val gpios = mutableSetOf<Gpio>()

    @Throws(IOException::class)
    fun register() {
        PeripheralManager.getInstance().run {
            csPin = openGpio(csGpio)
            clockPin = openGpio(clockGpio)
            mosiPin = openGpio(mosiGpio)
            misoPin = openGpio(misoGpio)
        }.also {
            listOf(csPin, clockPin, mosiPin, misoPin).forEach {
                it.setDirection(
                        if (it != misoPin) Gpio.DIRECTION_OUT_INITIALLY_LOW else Gpio.DIRECTION_IN)
                gpios.add(it)
            }
        }
    }

    @Throws(IOException::class)
    fun readAdc(channel: Int): Int {
        if (channel < 0 || channel > 7) {
            throw IOException("ADC channel must be between 0 and 7")
        }

        initReadState()
        initChannelSelect(channel)
        return getValueFromSelectedChannel()
    }

    @Throws(IOException::class)
    private fun initReadState() {
        csPin.value = true
        clockPin.value = false
        csPin.value = false
    }

    @Throws(IOException::class)
    private fun initChannelSelect(channel: Int) {
        var commandOut = channel
        commandOut = commandOut or 0x18 // start bit + single-ended bit
        commandOut = commandOut shl 0x3 // we only need to send 5 bits

        for (i in 0..4) {
            mosiPin.value = commandOut and 0x80 != 0x0
            commandOut = commandOut shl 0x1
            toggleClock()
        }
    }

    @Throws(IOException::class)
    private fun getValueFromSelectedChannel(): Int {
        var value = 0x0

        for (i in 0..11) {
            toggleClock()

            value = value shl 0x1
            if (misoPin.value) {
                value = value or 0x1
            }
        }

        csPin.value = true
        value = value shr 0x1 // first bit is 'null', so drop it
        return value
    }

    @Throws(IOException::class)
    private fun toggleClock() {
        clockPin.value = true
        clockPin.value = false
    }

    fun unregister() {
        for (gpio in gpios) {
            try {
                gpio.close()
            } catch (ignore: IOException) {
                // do nothing
            }
        }
    }
}
