package com.tomaszrykala.midionandroid.control

class MidiPot(private val midiEventListener: MidiEventListener,
              val midiChannel: Byte, val key: Byte) : CcListener {

    override fun onChange(change: Int) {
        midiEventListener.onControlChange(this, change.toByte())
    }
}