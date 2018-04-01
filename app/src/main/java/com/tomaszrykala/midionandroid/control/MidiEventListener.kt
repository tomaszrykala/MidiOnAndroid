package com.tomaszrykala.midionandroid.control

interface MidiEventListener {
    fun onNoteOn(midiButton: MidiButton, pressed: Boolean)
    fun onControlChange(midiPot: MidiPot, velocity: Byte)
}