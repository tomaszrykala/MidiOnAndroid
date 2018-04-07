package com.tomaszrykala.midionandroid.control.adc

import com.tomaszrykala.midionandroid.control.Keys
import com.tomaszrykala.midionandroid.control.MidiEventListener
import com.tomaszrykala.midionandroid.control.MidiThingsPot
import com.tomaszrykala.midionandroid.driver.Driver

class McpDriverManager(private val midiEventListener: MidiEventListener, driver: Driver) {

    companion object {
        const val mixerAdcStartChannel = 10
    }

    private val keys = Keys.Octave0.Key.values()

    private val mixerMcpDriver: McpDriver = McpDriver(driver.getSpio0(), driver.getSclk(), driver.getMosi(), driver.getMiso())
    private val mixerMidiPots: MutableList<MidiThingsPot> = mutableListOf()

    fun start() {
        mixerMcpDriver.start()
        (0 until keys.size).mapTo(mixerMidiPots) {
            MidiThingsPot(midiEventListener, (mixerAdcStartChannel).toByte(), keys[it].pitch.toByte(),
                    mixerMcpDriver, it).apply { start() }
        }
    }

    fun stop() {
        mixerMidiPots.forEach { it.stop() }
        mixerMcpDriver.stop()
    }
}