package com.tomaszrykala.midionandroid.control.adc

import com.tomaszrykala.midionandroid.Key
import com.tomaszrykala.midionandroid.control.MidiEventListener
import com.tomaszrykala.midionandroid.control.MidiThingsPot
import com.tomaszrykala.midionandroid.driver.Driver

class McpDriverManager(private val midiEventListener: MidiEventListener, driver: Driver) {

    companion object {
        const val mixerAdcStartChannel = 10
    }

    private val keys: DoubleArray = doubleArrayOf(Key.C0, Key.Cs0, Key.D0, Key.Ds0, Key.E0, Key.F0, Key.Fs0, Key.G0)

    private val mixerMcpDriver: McpDriver = McpDriver(driver.getSpio0(), driver.getSclk(), driver.getMosi(), driver.getMiso())
    private val mixerMidiPots: MutableList<MidiThingsPot> = mutableListOf()

    fun start() {
        mixerMcpDriver.start()
        (0 until keys.size).mapTo(mixerMidiPots) {
            MidiThingsPot(midiEventListener, (mixerAdcStartChannel).toByte(), keys[it].toByte(),
                    mixerMcpDriver, it).apply { start() }
        }
    }

    fun stop() {
        mixerMidiPots.forEach { it.stop() }
        mixerMcpDriver.stop()
    }
}