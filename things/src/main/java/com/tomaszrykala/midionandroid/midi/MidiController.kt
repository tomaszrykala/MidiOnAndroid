package com.tomaszrykala.midionandroid.midi

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.util.Log
import com.tomaszrykala.midi.MidiEvent
import com.tomaszrykala.midi.MidiEventType
import com.tomaszrykala.midionandroid.control.MidiButton
import com.tomaszrykala.midionandroid.control.MidiEventListener
import com.tomaszrykala.midionandroid.control.MidiPot

class MidiController(context: Context,
                     private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager)
    : MidiEventListener {

    companion object {
        const val TAG: String = "MidiController"
    }

    private var midiInputPort: MidiInputPort? = null
    private var midiDevice: MidiDevice? = null

    override fun onNote(midiButton: MidiButton, pressed: Boolean) {
        send(MidiEvent(
                if (pressed) MidiEventType.STATUS_NOTE_ON
                else MidiEventType.STATUS_NOTE_OFF,
                midiButton.midiChannel, midiButton.key, midiButton.velocity))
    }

    override fun onControlChange(midiPot: MidiPot, velocity: Byte) {
        send(MidiEvent(MidiEventType.STATUS_CONTROL_CHANGE, midiPot.midiChannel, midiPot.key, velocity))
    }

    private fun send(event: MidiEvent) {
        Log.d(TAG, event.toString())
        byteArrayOf((event.type.byte + event.channel).toByte(), event.note, event.pressure, event.channel)
                .apply { midiInputPort?.send(this, 0, size) }
    }

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

    fun close() {
        midiInputPort?.close()
        midiInputPort = null
        midiDevice?.close()
        midiDevice = null
    }
}
