package com.tomaszrykala.midi

import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.util.Log

class BaseMidiController(private val midiManager: MidiManager) {

    companion object {
        const val TAG: String = "MidiController"
    }

    private var midiInputPort: MidiInputPort? = null
    private var midiDevice: MidiDevice? = null

    fun open(midiDeviceInfo: MidiDeviceInfo) =
            close().also {
                midiDeviceInfo
                        .ports.first { it.type == MidiDeviceInfo.PortInfo.TYPE_INPUT }
                        .portNumber.also { portNumber ->
                    midiManager.openDevice(midiDeviceInfo, {
                        midiDevice = it
                        midiInputPort = it.openInputPort(portNumber)
                    }, Handler())
                }
            }

    fun send(event: MidiEvent) {
        Log.d(TAG, event.toString())
        byteArrayOf((event.type.byte + event.channel).toByte(), event.note, event.pressure, event.channel)
                .apply { midiInputPort?.send(this, 0, size) }
    }

    fun close() {
        midiInputPort?.close()
        midiInputPort = null
        midiDevice?.close()
        midiDevice = null
    }
}
