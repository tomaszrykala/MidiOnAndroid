package com.tomaszrykala.midi

data class MidiEvent(val type: MidiEventType, val channel: Byte, val note: Byte, val pressure: Byte)

enum class MidiEventType(val byte: Byte) {

    STATUS_NOTE_OFF(0x80.toByte()),
    STATUS_NOTE_ON(0x90.toByte()),
    STATUS_CONTROL_CHANGE(0xB0.toByte())
}