package com.tomaszrykala.midionandroid.control

class MidiPot(private val midiEventListener: MidiEventListener,
              val midiChannel: Byte, val key: Byte) : AnalogListener {

    override fun onChange(value: Int) {
        midiEventListener.onControlChange(this, value.toByte())
    }
}