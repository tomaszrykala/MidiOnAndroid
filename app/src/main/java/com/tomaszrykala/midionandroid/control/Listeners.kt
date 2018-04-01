package com.tomaszrykala.midionandroid.control

interface CcListener {
    fun onChange(change: Int)
}

interface MidiEventListener {
    fun onNote(midiButton: MidiButton, pressed: Boolean)
    fun onControlChange(midiPot: MidiPot, velocity: Byte)
}