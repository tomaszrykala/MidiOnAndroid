package com.tomaszrykala.midionandroid.event

data class MidiEvent(val type: MidiEventType, val channel: Byte, val note: Byte, val pressure: Byte) {

//    constructor(type: MidiEventType, channel: Byte, note: Byte, pressure: Byte) :
//            this(type, byteArrayOf(channel, note, pressure))

//    fun type(): Byte = type.byte
//    fun channel(): Byte = data[0]
//    fun note(): Byte = data[1]
//    fun pressure(): Byte = data[2]
}