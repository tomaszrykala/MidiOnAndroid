package com.tomaszrykala.midionandroid.control

import com.tomaszrykala.midionandroid.control.adc.McpDriver

class MidiThingsPot(midiEventListener: MidiEventListener, midiChannel: Byte, key: Byte,
                    private val mcpDriver: McpDriver, private val analogChannel: Int)
    : MidiPot(midiEventListener, midiChannel, key) {

    fun start() {
        mcpDriver.addListener(analogChannel, this)
    }

    fun stop() {
        mcpDriver.addListener(analogChannel, null)
    }
}